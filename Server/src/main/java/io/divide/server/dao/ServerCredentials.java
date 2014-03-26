package io.divide.server.dao;

import io.divide.shared.util.ReflectionUtils;
import io.divide.shared.web.transitory.Credentials;
import io.divide.shared.web.transitory.TransientObject;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 9/2/13
 * Time: 11:33 AM
 */
public class ServerCredentials extends Credentials {

    public ServerCredentials(TransientObject serverObject){
        try {
            Map meta = (Map) ReflectionUtils.getObjectField(serverObject, TransientObject.META_DATA);
            Map user = (Map) ReflectionUtils.getObjectField(serverObject,TransientObject.USER_DATA);

            ReflectionUtils.setObjectField(this, TransientObject.META_DATA, meta);
            ReflectionUtils.setObjectField(this, TransientObject.USER_DATA, user);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setOwnerId(Integer id){
        super.setOwnerId(id);
    }

}
