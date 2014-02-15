package com.jug6ernaut.network.authenticator.server.appengine;

import com.jug6ernaut.network.shared.web.transitory.TransientObject;

import java.util.Map;

/**
 * Created by williamwebb on 11/16/13.
 */
public class TestObject2 extends TransientObject {

    public TestObject2(){
        super(TestObject2.class);
    }

    protected TestObject2(TransientObject o) {
        super(TestObject2.class);
        this.setMaps(o.getUserData(),o.getMetaData());
        meta_data.put(OBJECT_TYPE_KEY.KEY,TestObject2.class.getName()); // dont allow null
    }

    public void setMaps(Map<String,Object> userData, Map<String,String> metaData){
        this.user_data = userData;
        this.meta_data = metaData;
    }
}
