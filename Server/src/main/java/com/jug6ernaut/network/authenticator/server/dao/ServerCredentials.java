package com.jug6ernaut.network.authenticator.server.dao;

import com.jug6ernaut.network.shared.web.transitory.Credentials;
import com.jug6ernaut.network.shared.web.transitory.TransientObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 9/2/13
 * Time: 11:33 AM
 */
public class ServerCredentials extends Credentials {

    public ServerCredentials(TransientObject serverObject){
        Map userData = new HashMap<String,Object>(serverObject.getUserData());
        Map metaData = new HashMap<String,String>(serverObject.getMetaData());

        this.user_data = userData;
        this.meta_data = metaData;

        // not creating copy it seems
//        try {
//            ReflectionUtils.setObjectField(this, TransientObject.META_DATA, metaData);
//            ReflectionUtils.setObjectField(this, TransientObject.USER_DATA, userData);
//        } catch (Exception e) {
//            throw new RuntimeException();
//        }
    }

    @Override
    public void setOwnerId(Integer id){
        super.setOwnerId(id);
    }

}
