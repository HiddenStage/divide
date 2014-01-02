package com.jug6ernaut.network.authenticator.client.auth.credentials;

import com.jug6ernaut.network.shared.web.transitory.Credentials;

/**
 * Created by williamwebb on 10/26/13.
 *
 * Credentials used for Signing up
 */
public class SignUpCredentials extends Credentials {

    public SignUpCredentials(String username, String email, String password){
        this.setUsername(username);
        this.setEmailAddress(email);
        this.setPassword(password);
    }
}
