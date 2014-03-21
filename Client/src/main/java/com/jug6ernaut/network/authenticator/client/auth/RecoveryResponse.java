package com.jug6ernaut.network.authenticator.client.auth;

import com.jug6ernaut.network.authenticator.client.BackendUser;
import com.jug6ernaut.network.authenticator.client.data.ServerResponse;
import com.jug6ernaut.network.authenticator.client.http.Status;

/**
 * Created by williamwebb on 1/6/14.
 */
public class RecoveryResponse extends ServerResponse<BackendUser> {
    protected RecoveryResponse(BackendUser backendUser, Status status, String error) {
        super(backendUser, status, error);
    }
}
