/*
 * Copyright (C) 2014 Divide.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
