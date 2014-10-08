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
