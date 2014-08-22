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

import io.divide.dao.ServerDAO;
import io.divide.server.auth.SecManager;
import io.divide.server.dao.DAOManager;
import io.divide.server.dao.ServerCredentials;
import io.divide.shared.transitory.Credentials;
import io.divide.shared.transitory.query.QueryBuilder;
import io.divide.shared.util.AuthTokenUtils;
import org.mindrot.jbcrypt.BCrypt;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

@Path("/test")
public class TestEndpoint {
    private Logger logger = Logger.getLogger(TestEndpoint.class.getName());

    @Inject
    SecManager securityManager;
    @Context DAOManager dao;

    @PostConstruct
    private void addPaths(){
        securityManager.addSafePath("/test/setup");
        securityManager.addSafePath("/test/teardown");
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/setup")
    public Response setup() throws Exception{
//        logger.info("setup");
//        Credentials user = TestUtils.getTestUser();
//        user = new ServerCredentials(user);
//        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(10)));

        ServerCredentials toSave = new ServerCredentials(TestUtils.getTestUser());

//        String en = toSave.getPassword();
//        toSave.decryptPassword(keyManager.getPrivateKey()); //decrypt the password
//        String de = toSave.getPassword();
        String ha = BCrypt.hashpw(toSave.getPassword(), BCrypt.gensalt(10));
        toSave.setPassword(ha); //hash the password for storage
        toSave.setAuthToken(AuthTokenUtils.getNewToken(securityManager.getSymmetricKey(), toSave));
        toSave.setRecoveryToken(AuthTokenUtils.getNewToken(securityManager.getSymmetricKey(), toSave));
        toSave.setOwnerId(dao.count(Credentials.class.getName()) + 1);

        dao.save(toSave);
        return Response.ok().entity(toSave).build();
    }

    @GET
    @Path("/teardown")
    public Response tearDown(){
        logger.info("teardown");

        try {
            dao.query(new QueryBuilder().delete().from(Credentials.class).build());
        } catch (ServerDAO.DAOException e) {
            e.printStackTrace();
        }

        return Response.ok().build();
    }
}
