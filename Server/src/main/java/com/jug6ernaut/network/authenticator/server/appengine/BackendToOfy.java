package com.jug6ernaut.network.authenticator.server.appengine;

import com.jug6ernaut.network.authenticator.server.dao.ServerObject;
import com.jug6ernaut.network.shared.util.ReflectionUtils;
import com.jug6ernaut.network.shared.web.transitory.TransientObject;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 8/5/13
 * Time: 4:56 PM
 */

public class BackendToOfy{

    private BackendToOfy(){}

    public static OfyObject getOfy(TransientObject transientObject) throws NoSuchFieldException, IllegalAccessException {
        OfyObject oo = new OfyObject();
//        oo.meta_data = (Map<String,String>) ReflectionUtils.getObjectField(transientObject, TransientObject.META_DATA);
        oo.user_data = (Map<String,String>) ReflectionUtils.getObjectField(transientObject, TransientObject.USER_DATA);
        oo.object_key = transientObject.getObjectKey();
        return oo;
    }

    public static TransientObject getBack(OfyObject ofyObject) throws NoSuchFieldException, IllegalAccessException {
        TransientObject beo = new ServerObject("dummy"); // gonna get over written anyways
        // Dont set the object_key, its included in the meta_data
//        ReflectionUtils.setObjectField(beo, TransientObject.META_DATA, ofyObject.meta_data);
        ReflectionUtils.setObjectField(beo, TransientObject.USER_DATA, ofyObject.user_data);
        return beo;
    }

}