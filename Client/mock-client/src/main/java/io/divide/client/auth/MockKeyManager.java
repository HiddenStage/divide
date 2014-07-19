package io.divide.client.auth;

import io.divide.shared.server.KeyManager;
import io.divide.shared.util.Crypto;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Created by williamwebb on 4/14/14.
 */
public class MockKeyManager implements KeyManager {

    private String encryptionKey;
    private String pushKey;
    private KeyPair keyPair;

    public MockKeyManager(String encryptionKey) throws NoSuchAlgorithmException {
        this.encryptionKey = encryptionKey;
        this.keyPair = Crypto.getNew();
    }

    public PublicKey getPublicKey(){
        return keyPair.getPublic();
    }

    public PrivateKey getPrivateKey(){
        return keyPair.getPrivate();
    }

    public String getSymmetricKey(){
        return encryptionKey;
    }

    public String getPushKey() {
        return pushKey;
    }

}
