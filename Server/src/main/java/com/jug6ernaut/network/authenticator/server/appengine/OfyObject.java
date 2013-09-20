package com.jug6ernaut.network.authenticator.server.appengine;

import com.googlecode.objectify.annotation.EmbedMap;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

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
    public Map<String,String> user_data;

    @Index
    @EmbedMap
    public Map<String,String> meta_data;

    @Override
    public String toString() {
        return "OfyObject{" +
                "object_key='" + object_key + '\'' +
                ", user_data=" + user_data +
                ", meta_data=" + meta_data +
                '}';
    }
}
