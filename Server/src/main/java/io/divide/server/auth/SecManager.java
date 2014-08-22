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

package io.divide.server.auth;

import io.divide.dao.ServerDAO;
import io.divide.shared.server.KeyManager;
import io.divide.shared.util.Crypto;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SecManager implements KeyManager {

    private ServerDAO dao;
    private String encryptionKey;
    private String pushKey;

    public SecManager(ServerDAO dao, String encryptionKey){
        this.dao = dao;
        this.encryptionKey = encryptionKey;
    }

    private KeyPair cachedKeys = null;
    private synchronized KeyPair getKeys(){
        if(cachedKeys!=null) return cachedKeys;

        cachedKeys = dao.keys(null);
        if(cachedKeys == null){
            try {
                cachedKeys = Crypto.getNew();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            dao.keys(cachedKeys);
        }

        return cachedKeys;
    }

    public PublicKey getPublicKey(){
        return getKeys().getPublic();
    }

    public PrivateKey getPrivateKey(){
        return getKeys().getPrivate();
    }

    public String getSymmetricKey(){
        return encryptionKey;
    }

    @Override
    public String getPushKey() {
        return pushKey;
    }

    private List<String> safePaths = new ArrayList<String>(Arrays.asList(
            "",
            "/"
    ));

    public List<String> getSafePaths(){
        return safePaths;
    }

    public void addSafePath(String path){
        safePaths.add(path);
    }
}
