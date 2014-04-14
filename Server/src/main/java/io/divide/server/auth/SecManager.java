package io.divide.server.auth;

import io.divide.dao.ServerDAO;
import io.divide.shared.server.KeyManager;
import io.divide.shared.util.Crypto;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by williamwebb on 1/12/14.
 */
public class SecManager implements KeyManager {

    ServerDAO dao;
    private String encryptionKey;

    public SecManager(ServerDAO dao, String encryptionKey){
        this.dao = dao;
        this.encryptionKey = encryptionKey;
    };

    private KeyPair cachedKeys = null;
    private synchronized KeyPair getKeys(){
        if(cachedKeys!=null) return cachedKeys;

        cachedKeys = dao.keys(null);
        if(cachedKeys == null){
            PrivateKey priKey = null;
            PublicKey pubKey = null;
            try {
                priKey = Crypto.get().getPrivate();
                pubKey = Crypto.get().getPublic();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            cachedKeys = new KeyPair(pubKey,priKey);
            dao.keys(cachedKeys);
        }

        return cachedKeys;
    }

    public PublicKey getPublicKey(){
        return getKeys().getPublic();
    }

    public PrivateKey getPrivateKey(){
        return getKeys().getPrivate();
    }

    public String getSymmetricKey(){
        return encryptionKey;
    }

    private List<String> safePaths = new ArrayList<String>(Arrays.asList(
            "",
            "/"
    ));

    public List<String> getSafePaths(){
        return safePaths;
    }

    public void addSafePath(String path){
        safePaths.add(path);
    }
}
