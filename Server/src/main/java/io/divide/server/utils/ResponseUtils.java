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

package io.divide.server.utils;

import io.divide.dao.ServerDAO;
import io.divide.shared.transitory.Credentials;

import javax.ws.rs.core.Response;

public class ResponseUtils {

    public static Response notAuthReponse(String string){
        return Response
                .status(Response.Status.UNAUTHORIZED)
                .entity(string + "\r\n")
                .build();
    }

    public static Response fromDAOExpection(ServerDAO.DAOException exception){
        return Response
                .status(exception.getStatusCode())
                .build();
    }

    public static Response ok(Credentials credentials){
        return Response
                .ok()
                .entity(credentials)
                .build();
    }

    public static Response errorResponse(Throwable error){
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error).build();
    }
}
