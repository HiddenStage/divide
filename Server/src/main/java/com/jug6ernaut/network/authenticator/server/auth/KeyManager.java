package com.jug6ernaut.network.authenticator.server.auth;

import com.jug6ernaut.network.authenticator.server.dao.DAOManager;
import com.jug6ernaut.network.shared.util.Crypto;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Created by williamwebb on 1/12/14.
 */
public class KeyManager {

    DAOManager dao;

    public KeyManager(DAOManager dao){
        this.dao = dao;
    };


    private KeyPair cachedKeys = null;
    private KeyPair getKeys(){
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
        return getKeys().getPrivate();    }
}
