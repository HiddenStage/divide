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
