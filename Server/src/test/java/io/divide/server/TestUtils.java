/*
 * Copyright (C) 2014 Divide.io
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.divide.server;

import com.google.gson.Gson;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.record.ODatabaseRecord;
import io.divide.dao.ServerDAO;
import io.divide.dao.orientdb.OrientDBDao;
import io.divide.server.dao.ServerCredentials;
import io.divide.shared.transitory.Credentials;
import io.divide.shared.transitory.FilePermissions;
import io.divide.shared.transitory.TransientObject;
import io.divide.shared.transitory.query.QueryBuilder;
import io.divide.shared.util.AuthTokenUtils;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

public class TestUtils {

    public static final String KEY = "saywhatwhat";

    public static class TestWrapper{
        private ODatabaseRecord db;
        public ServerDAO serverDao;
        public AuthApplication app;
        public long time;

        public void tearDown() throws ServerDAO.DAOException {
//            db.release();
            serverDao.query(new QueryBuilder().delete().from(Credentials.class).build());
            db.drop();
            db.close();
        }
    }

    public static TestWrapper setUp(){
        TestWrapper container = new TestWrapper();
        container.time = System.nanoTime();
        container.db = new ODatabaseDocumentTx(OrientDBDao.DEFAULT_CONFIG);
        if(container.db.exists()){
            container.db.open("admin","admin");
        } else {
            container.db.create();
        }
        container.serverDao = new OrientDBDao((ODatabaseDocument) container.db);
        try {
            container.serverDao.query(new QueryBuilder().delete().from(Credentials.class).build());
        } catch (ServerDAO.DAOException e) {
            e.printStackTrace();
        }
        container.app = new TestApplication(container.serverDao);

        return container;
    }

    public static Credentials getTestUser() {
        Credentials c = new Credentials("someUsername","someEmail","somePassword");
        ServerCredentials sc = new ServerCredentials(c);
        sc.setOwnerId(1);
        FilePermissions fp = sc.getFilePermissions();
        fp.setReadable(true, FilePermissions.Level.WORLD);
        fp.setWritable(true, FilePermissions.Level.WORLD);
        sc.setFilePermissions(fp);
        sc.setAuthToken(AuthTokenUtils.getNewToken(KEY, sc));
        sc.setRecoveryToken(AuthTokenUtils.getNewToken(KEY, sc));
        return sc;
    }

    public static Entity<String> toEntity(Object o){
        return Entity.entity(getGson().toJson(o), MediaType.APPLICATION_JSON_TYPE);
    }

    public static Entity<String > getTestUserEntity() throws Exception {
        return Entity.entity(getGson().toJson(getTestUser()), MediaType.APPLICATION_JSON_TYPE);
//        return Entity.json(getTestUser());
    }

    public static <T extends TransientObject> T convert(TransientObject o, Class<T> to){
        return gson.fromJson(gson.toJson(o),to);
    }

    private static Gson gson = new Gson();
    public static Gson getGson(){
        return gson;
    }

}
