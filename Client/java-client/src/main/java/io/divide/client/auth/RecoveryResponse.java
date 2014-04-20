package io.divide.client.auth;

import io.divide.client.BackendUser;
import io.divide.client.data.ServerResponse;
import io.divide.client.http.Status;

/**
 * Created by williamwebb on 1/6/14.
 */
public class RecoveryResponse extends ServerResponse<BackendUser> {
    protected RecoveryResponse(BackendUser backendUser, Status status, String error) {
        super(backendUser, status, error);
    }
}
