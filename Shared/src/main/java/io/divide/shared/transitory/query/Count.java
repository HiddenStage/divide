package io.divide.shared.transitory.query;

import io.divide.shared.transitory.TransientObject;

/**
 * Created by williamwebb on 2/5/14.
 */
public class Count extends TransientObject {
    public Count(int count, String from) {
        super(Count.class);
        setCount(count);
        setFrom(from);
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
