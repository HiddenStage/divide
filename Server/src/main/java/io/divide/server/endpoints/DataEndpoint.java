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

package io.divide.server.endpoints;

import io.divide.server.dao.DAOManager;
import io.divide.server.dao.Session;
import io.divide.server.utils.ResponseUtils;
import io.divide.dao.ServerDAO;
import io.divide.shared.util.ObjectUtils;
import io.divide.shared.transitory.Credentials;
import io.divide.shared.transitory.TransientObject;
import io.divide.shared.transitory.query.Query;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.logging.Logger;

@Path("/data")
public class DataEndpoint {
    private Logger logger = Logger.getLogger(DataEndpoint.class.getName());

    @Context
    DAOManager dao;

    @POST
    @Path("/get/{objectType}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response get( @PathParam("objectType") String objectType, Collection<String> keys) {
        try { logger.info("get: " + keys);
            return Response
                    .ok()
                    .entity(dao.get(objectType,ObjectUtils.c2v(keys)))
                    .build();
        }catch (ServerDAO.DAOException e) {
            return ResponseUtils.fromDAOExpection(e);
        }
    }

    @POST
    @Path("/query")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response query(@Context Session session,Query query) {
        try { logger.info("query: " + query);
            // not allowed to query credentials type
            if(query.getFrom().equals(Credentials.class.getName())){
                return Response.status(Response.Status.FORBIDDEN).entity("Query of Credentials is FORBIDDEN").build();
            }
            return Response
                    .ok()
                    .entity(dao.query(query))
                    .build();
        }catch (ServerDAO.DAOException e) {
            return ResponseUtils.fromDAOExpection(e);
        }
    }

    @POST
    @Path("/save")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response save(Collection<ServerObject> objects) {
        try { logger.info("save: " + objects);
            dao.save(ObjectUtils.c2v(objects));

            return Response
                    .ok()
                    .build();
        }catch (ServerDAO.DAOException e) {
            return ResponseUtils.fromDAOExpection(e);
        }
    }

    @GET
    @Path("/count/{objectType}")
    public Response count(@Context Session session, @PathParam("objectType") String objectType) {
        try { logger.info("count: " + objectType);
            int count = dao.count(objectType);

            return Response
                    .ok()
                    .entity(count)
                    .build();
        }catch (Exception e) {
            logger.severe(ExceptionUtils.getStackTrace(e));
            return Response.serverError().build();
        }
    }


    private static class ServerObject extends TransientObject{

        private ServerObject() {
            super(TransientObject.class);
        }

        @Override
        protected Credentials getLoggedInUser(){
            return new Credentials("","",""){

                @Override
                protected boolean isSystemUser(){
                    return true;
                }

            };
        }
    }

}
