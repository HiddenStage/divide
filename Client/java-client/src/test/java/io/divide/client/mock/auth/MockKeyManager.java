package io.divide.client.mock.auth;

import io.divide.shared.server.KeyManager;
import io.divide.shared.util.Crypto;

import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Created by williamwebb on 4/14/14.
 */
public class MockKeyManager implements KeyManager {

    private String encryptionKey;
    private PrivateKey privateKey;
    private PublicKey publicKey;

    public MockKeyManager(String encryptionKey) throws NoSuchAlgorithmException {
        this.encryptionKey = encryptionKey;
        this.privateKey = Crypto.get().getPrivate();
        this.publicKey = Crypto.get().getPublic();
    }

    public PublicKey getPublicKey(){
        return publicKey;
    }

    public PrivateKey getPrivateKey(){
        return privateKey;
    }

    public String getSymmetricKey(){
        return encryptionKey;
    }

    @Override
    public String getPushKey() {
        return null;
    }

}
