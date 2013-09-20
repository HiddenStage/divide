package com.jug6ernaut.network.authenticator.server.dao;

import com.jug6ernaut.network.authenticator.server.auth.UserContext;
import com.jug6ernaut.network.shared.web.transitory.Credentials;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 9/13/13
 * Time: 9:38 PM
 */
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
