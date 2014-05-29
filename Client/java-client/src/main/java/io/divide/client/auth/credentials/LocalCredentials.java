package io.divide.client.auth.credentials;

/**
 * Created by williamwebb on 3/28/14.
 */
public class LocalCredentials {
    private String emailAddress;
    private String authToken;
    private String recoveryToken;

    public String getName() {
        return emailAddress;
    }

    public void setName(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getRecoveryToken() {
        return recoveryToken;
    }

    public void setRecoveryToken(String recoveryToken) {
        this.recoveryToken = recoveryToken;
    }

    @Override
    public String toString() {
        return "LocalCredentials{" +
                "emailAddress='" + emailAddress + '\'' +
                ", authToken='" + authToken + '\'' +
                ", recoveryToken='" + recoveryToken + '\'' +
                '}';
    }
}
