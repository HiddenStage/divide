package io.divide.dao.appengine;

import com.googlecode.objectify.annotation.EmbedMap;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 8/23/13
 * Time: 8:09 PM
 */
@Entity
public class OfyObject {

    @Id
    public String object_key;

    @Index
    @EmbedMap
    public Map<String,Object> user_data = new HashMap<String, Object>(0);

    @Index
    @EmbedMap
    public Map<String,String> meta_data = new HashMap<String, String>(0);

    public OfyObject(){}

    public OfyObject(String key,Map<String,Object> userData, Map<String,String> metaData){
        this.object_key = key;
        this.user_data = userData;
        this.meta_data = metaData;
    }

    @Override
    public String toString() {
        return "OfyObject{" +
                "object_key='" + object_key + '\'' +
                ", user_data=" + user_data +
                ", meta_data=" + meta_data +
                '}';
    }
}
