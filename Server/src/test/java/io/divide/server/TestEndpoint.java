package io.divide.server;

import io.divide.server.auth.SecManager;
import io.divide.server.dao.DAOManager;
import io.divide.server.dao.ServerCredentials;
import io.divide.dao.DAO;
import io.divide.shared.util.AuthTokenUtils;
import io.divide.shared.web.transitory.Credentials;
import io.divide.shared.web.transitory.query.QueryBuilder;
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

/**
 * Created by williamwebb on 3/14/14.
 */
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
        toSave.setAuthToken(AuthTokenUtils.getNewToken(securityManager.getKey(), toSave));
        toSave.setRecoveryToken(AuthTokenUtils.getNewToken(securityManager.getKey(), toSave));
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
        } catch (DAO.DAOException e) {
            e.printStackTrace();
        }

        return Response.ok().build();
    }
}
