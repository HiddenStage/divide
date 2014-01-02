package com.jug6ernaut.network.authenticator.client.auth;

import com.jug6ernaut.network.authenticator.client.BackendUser;
import com.jug6ernaut.network.authenticator.client.ServerResponse;
import com.jug6ernaut.network.authenticator.client.http.Status;

/**
 * Created by williamwebb on 12/21/13.
 */
public class SignInResponse extends ServerResponse<BackendUser> {

    public SignInResponse(BackendUser user, Status status, String error) {
        super(user, status, error);
    }
}

