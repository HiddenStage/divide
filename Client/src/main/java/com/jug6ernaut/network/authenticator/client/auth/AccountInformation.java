package com.jug6ernaut.network.authenticator.client.auth;

/**
 * Created by williamwebb on 12/30/13.
 */
public interface AccountInformation {

    public String getAccountName();
    public String getAccountType();
    public String getFullAccessType();
    public String getFullAccessLabel();
    public String getReadAccessType();
    public String getReadAccessLabel();
}
