package com.jug6ernaut.network.authenticator.server.appengine;

import com.jug6ernaut.network.shared.web.transitory.TransientObject;

import java.util.Map;

/**
 * Created by williamwebb on 11/16/13.
 */
public class TestObject1 extends TransientObject {

    public TestObject1(){
        super(TestObject1.class);
    }

    protected <T extends TransientObject> TestObject1(TransientObject o) {
        super(TestObject1.class);
        this.setMaps(o.getUserData(), o.getMetaData());
        meta_data.put(OBJECT_TYPE_KEY.KEY,TestObject1.class.getName()); // dont allow null
    }

    public void setMaps(Map<String,Object> userData, Map<String,String> metaData){
        this.user_data = userData;
        this.meta_data = metaData;
    }
}
