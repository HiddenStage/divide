package com.jug6ernaut.network.authenticator.client.data;

import com.jug6ernaut.network.authenticator.client.BackendObject;

/**
 * Created by williamwebb on 2/6/14.
 */
public class CountObject extends BackendObject {

    public CountObject(){
        super(CountObject.class);
    }

    public int getCount(){
        return get(Integer.class,"count");
    }

    public String getFrom(){
        return get(String.class,"from");
    }

    public void setCount(int count){
        put("count",count);
    }

    public void setFrom(String from){
        put("from",from);
    }
}
