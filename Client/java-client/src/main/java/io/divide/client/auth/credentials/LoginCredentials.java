package io.divide.client.auth.credentials;

import io.divide.shared.web.transitory.Credentials;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Created by williamwebb on 10/26/13.
 *
 * Credentials used for logging in
 */
public class LoginCredentials extends Credentials {

    private boolean isEncrypted = false;

    public LoginCredentials(String email, String password){
        this.setEmailAddress(email);
        this.setPassword(password);
    }

    @Override
    public void decryptPassword(PrivateKey privateKey){
        if(isEncrypted()){
            super.decryptPassword(privateKey);
            setEncrypted(false);
        }
    }

    @Override
    public void encryptPassword(PublicKey publicKey){
        if(!isEncrypted()){
            super.encryptPassword(publicKey);
            setEncrypted(true);
        }
    }

    public boolean isEncrypted(){
        return isEncrypted;
    }

    public void setEncrypted(boolean isEncrypted){
        this.isEncrypted = isEncrypted;
    }

}
