package io.divide.server.dao;

import io.divide.shared.web.transitory.Credentials;
import io.divide.shared.web.transitory.TransientObject;

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
