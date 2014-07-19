package io.divide.dao.appengine;

import com.google.gson.Gson;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Created by williamwebb on 11/7/13.
 */
@Entity
public class KeyObject {

    private static final Gson gson = new Gson();
    @Id
    private long id = 1;


    String pubKey;
    String priKey;

    public KeyObject(){}
    public KeyObject(byte[] pubKey, byte[] priKey){
        setKeys(pubKey,priKey);
    }

    public void setKeys(byte[] pubKey, byte[] priKey){
        this.pubKey = gson.toJson(pubKey,byte[].class);
        this.priKey = gson.toJson(priKey,byte[].class);
    }

    public byte[] getPublicKey(){
        if(pubKey!=null) return gson.fromJson(pubKey,byte[].class);
        else return null;
    }

    public byte[] getPrivateKey(){
        if(priKey!=null) return gson.fromJson(priKey,byte[].class);
        else return null;
    }
}
