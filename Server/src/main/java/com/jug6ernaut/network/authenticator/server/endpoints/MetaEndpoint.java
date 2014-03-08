package com.jug6ernaut.network.authenticator.server.endpoints;

import com.jug6ernaut.network.authenticator.server.AuthApplication;
import com.jug6ernaut.network.authenticator.server.dao.DAOManager;
import com.jug6ernaut.network.authenticator.server.dao.Session;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

/**
 * Created by williamwebb on 12/22/13.
 */
@Path("/")
public class MetaEndpoint {

    public static final String VERSION = "V1.0";

        Logger logger = Logger.getLogger(MetaEndpoint.class.getName());

        @Context DAOManager dao;

        @Context AuthApplication app;


        /*
        currently failing as the decryption key is probably different
         */
    @Produces(MediaType.TEXT_PLAIN)
        @GET
        public Response apiInfo(@Context Session session){


            return Response.ok().entity(VERSION).build();
        }
}
