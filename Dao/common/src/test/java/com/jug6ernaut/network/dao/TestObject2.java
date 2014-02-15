package com.jug6ernaut.network.dao;

import com.jug6ernaut.network.shared.web.transitory.TransientObject;

/**
 * Created by williamwebb on 11/16/13.
 */
public class TestObject2 extends TransientObject {
    protected TestObject2(String... vals) {
        super(TestObject2.class);
        if(vals.length%2!=0) throw new  IllegalArgumentException("Must have valid pairs");
        for(int x=0; x<vals.length;x+=2){
            put(vals[x],vals[x+1]);
        }
    }

}
