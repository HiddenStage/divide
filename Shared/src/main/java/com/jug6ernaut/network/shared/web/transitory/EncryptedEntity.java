package com.jug6ernaut.network.shared.web.transitory;

import com.jug6ernaut.network.shared.util.Crypto;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 7/31/13
 * Time: 8:54 PM
 */
public class EncryptedEntity {
    protected Map<String,String> data = new HashMap<String, String>();
    protected transient PublicKey publicKey;
    protected transient PrivateKey privateKey;

    private EncryptedEntity(){};

    protected String encrypt(String string){
        return new String(Crypto.encrypt(string.getBytes(),publicKey));
    }

    protected String decrypt(String string){
        return new String(Crypto.decrypt(string.getBytes(),privateKey));
    }

    public static class Writter extends EncryptedEntity{

        public Writter(PublicKey publicKey) {
            this.publicKey = publicKey;
        }

        public void put(String key, String value){
            data.put(key,encrypt(value));
        }
    }

    public static class Reader extends EncryptedEntity{

        public Reader(PrivateKey privateKey){
            this.privateKey = privateKey;
        }

        public void setKey(PrivateKey privateKey){
            this.privateKey = privateKey;
        }

        public String get(String key){
            return decrypt(data.get(key));
        }

    }
}
