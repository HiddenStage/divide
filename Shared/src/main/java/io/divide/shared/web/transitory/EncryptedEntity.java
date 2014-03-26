package io.divide.shared.web.transitory;

import io.divide.shared.util.Crypto;
import org.apache.commons.codec.binary.Base64;

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
    private Map<String,String> data = new HashMap<String, String>();
    protected transient PublicKey publicKey;
    protected transient PrivateKey privateKey;

    private EncryptedEntity(){};

    private String decrypt(String string, PrivateKey privateKey){
        byte[] decoded = Base64.decodeBase64(string.getBytes());
        byte[] encrypted = Crypto.decrypt(decoded,privateKey);
        return new String(encrypted);
    }

    private String encrypt(String string, PublicKey publicKey){
        byte[] encrypted  = Crypto.encrypt(string.getBytes(),publicKey);
        return new String(Base64.encodeBase64(encrypted));
    }
    protected void put(String key, String value){
        data.put(key,encrypt(value,publicKey));
    }

    protected String get(String key){
        return decrypt(data.get(key), privateKey);
    }

    public static class Writter extends EncryptedEntity{

        public Writter(PublicKey publicKey) {
            this.publicKey = publicKey;
        }

        @Override
        public void put(String key, String value){
            super.put(key, value);
        }
    }

    public static class Reader extends EncryptedEntity{

        public Reader(PrivateKey privateKey){
            this.privateKey = privateKey;
        }

        public void setKey(PrivateKey privateKey){
            this.privateKey = privateKey;
        }

        @Override
        public String get(String key){
            return super.get(key);
        }

    }

}
