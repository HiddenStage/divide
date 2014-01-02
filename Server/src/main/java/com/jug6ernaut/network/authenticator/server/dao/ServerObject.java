package com.jug6ernaut.network.authenticator.server.dao;

import com.jug6ernaut.network.shared.web.transitory.Credentials;
import com.jug6ernaut.network.shared.web.transitory.TransientObject;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 8/26/13
 * Time: 8:14 PM
 */
public class ServerObject extends TransientObject {

    private static Credentials user = new SystemUser();

    public ServerObject() {
        super(ServerObject.class);
//        this.user_data = userData;
//        this.meta_data = metaData;
    }

    @Override
    protected final Credentials getLoggedInUser(){
        return user;
    }

    public void setMaps(Map<String,Object> userData, Map<String,String> metaData){
        this.user_data = userData;
        this.meta_data = metaData;
    }

    private static class SystemUser extends Credentials {

        @Override
        protected boolean isSystemUser(){
            return true;
        }

    }
}
