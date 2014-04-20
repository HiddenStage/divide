package io.divide.client.auth;

import io.divide.client.auth.credentials.LocalCredentials;

import java.util.List;

/**
 * Created by williamwebb on 3/29/14.
 */
public interface AccountStorage {

    public void addAcccount(LocalCredentials credentials);
    public void removeAccount(String accountName);
    public LocalCredentials getAccount(String accountName);
    public boolean isAuthenticated(String accountName);
    public void setAuthToken(String accountName, String token);
    public void setRecoveryToken(String accountName, String token);
    public List<LocalCredentials> getAccounts();
    public boolean exists(String name);

}
