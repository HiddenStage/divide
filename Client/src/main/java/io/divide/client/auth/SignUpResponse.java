package io.divide.client.auth;

import io.divide.client.BackendUser;
import io.divide.client.data.ServerResponse;
import io.divide.client.http.Status;

/**
 * Created by williamwebb on 12/21/13.
 */
public class SignUpResponse extends ServerResponse<BackendUser> {

    public SignUpResponse(BackendUser user, Status status, String error) {
        super(user, status, error);
    }
}