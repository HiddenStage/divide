package com.jug6ernaut.network.authenticator.server.endpoints;

import com.jug6ernaut.network.authenticator.server.AuthApplication;
import com.jug6ernaut.network.authenticator.server.dao.DAOManager;
import com.jug6ernaut.network.authenticator.server.dao.Session;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

/**
 * Created by williamwebb on 12/22/13.
 */
@Path("/")
public class MetaEndpoint {

        Logger logger = Logger.getLogger(MetaEndpoint.class.getName());

        @Context
        DAOManager dao;

        @Context
        AuthApplication app;


        /*
        currently failing as the decryption key is probably different
         */
        @GET
        public Response apiInfo(@Context Session session){


            return Response.ok().entity("V1.0").build();
        }
}
