package com.jug6ernaut.network.shared.web.transitory;

import com.jug6ernaut.network.shared.util.Crypto;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 7/31/13
 * Time: 8:54 PM
 */
public class EncryptedEntity {
    private byte[] cipherText;
    public EncryptedEntity(){}

    public EncryptedEntity(String plainText, PublicKey publicKey){
        setCipherText(plainText,publicKey);
    }

    public void setCipherText(String plainText, PublicKey publicKey){
        cipherText = Crypto.encrypt(plainText.getBytes(), publicKey);
    }

    public String getPlainText(PrivateKey privateKey){
        return new String(Crypto.decrypt(cipherText,privateKey));
    }

}
