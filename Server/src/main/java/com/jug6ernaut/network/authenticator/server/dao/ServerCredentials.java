package com.jug6ernaut.network.authenticator.server.dao;

import com.jug6ernaut.network.shared.util.ReflectionUtils;
import com.jug6ernaut.network.shared.web.transitory.Credentials;
import com.jug6ernaut.network.shared.web.transitory.TransientObject;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 9/2/13
 * Time: 11:33 AM
 */
public class ServerCredentials extends Credentials {

    public ServerCredentials(TransientObject serverObject){
        try {
//            ReflectionUtils.setObjectField(this, TransientObject.META_DATA, serverObject.getMetaData());
            ReflectionUtils.setObjectField(this, TransientObject.USER_DATA, serverObject.getUserData());
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void setUserId(String id){
        super.setUserId(id);
    }

}
