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

package io.divide.server.dao;

import io.divide.server.auth.UserContext;
import io.divide.shared.transitory.Credentials;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.util.logging.Logger;

//@Provider
public class Session {
    private Logger logger = Logger.getLogger(Session.class.getName());
    @Context
    HttpServletRequest request;

    public static final String SESSION_KEY = "SESSION_KEY";
    UserContext userContext;

    @PostConstruct
    private void setup(){
        setup((UserContext) request.getAttribute(SESSION_KEY));
    }

    public void setup(UserContext userContext){
        this.userContext = userContext;
        logger.info("loggedIn: " + loggedIn());
    }

    public boolean loggedIn(){
        return (userContext != null && userContext.getUser() != null);
    }

    public UriInfo getUriInfo(){
        return (userContext==null?null:userContext.getUriInfo());
    }

    public Credentials getUser(){
        return (userContext==null?null:userContext.getUser());
    }



}
