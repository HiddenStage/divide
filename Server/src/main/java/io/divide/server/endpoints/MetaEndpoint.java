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

import io.divide.server.AuthApplication;
import io.divide.server.dao.DAOManager;
import io.divide.server.dao.Session;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

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
