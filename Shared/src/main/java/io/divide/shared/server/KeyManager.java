package io.divide.shared.server;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Created by williamwebb on 4/11/14.
 */
public interface KeyManager {

    public PublicKey getPublicKey();
    public PrivateKey getPrivateKey();
    public String getSymmetricKey();
    public String getPushKey();
}
