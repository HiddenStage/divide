/*
 * Copyright (C) 2014 Divide.io
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.divide.client.auth;

import io.divide.shared.server.KeyManager;
import io.divide.shared.util.Crypto;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

public class MockKeyManager implements KeyManager {

    private String encryptionKey;
    private String pushKey;
    private KeyPair keyPair;

    public MockKeyManager(String encryptionKey) throws NoSuchAlgorithmException {
        this.encryptionKey = encryptionKey;
        this.keyPair = Crypto.getNew();
    }

    public PublicKey getPublicKey(){
        return keyPair.getPublic();
    }

    public PrivateKey getPrivateKey(){
        return keyPair.getPrivate();
    }

    public String getSymmetricKey(){
        return encryptionKey;
    }

    public String getPushKey() {
        return pushKey;
    }

}
