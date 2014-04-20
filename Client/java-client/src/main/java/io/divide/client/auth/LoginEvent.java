package io.divide.client.auth;

import io.divide.client.BackendUser;

/**
 * Created by williamwebb on 2/3/14.
 */
public class LoginEvent {
    public BackendUser user;
    public LoginState state;
}
