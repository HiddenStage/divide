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

package io.divide.shared.transitory;

import io.divide.shared.util.Base64;
import io.divide.shared.util.Crypto;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

/*
 * Network entity class which encrypts/decrypts data as it is added/retrieved.
 */
public class EncryptedEntity {
    private Map<String,String> data = new HashMap<String, String>();
    protected transient PublicKey publicKey;
    protected transient PrivateKey privateKey;

    private EncryptedEntity(){};

    private String decrypt(String string, PrivateKey privateKey){
        byte[] decoded = Base64.decode(string.getBytes());
        byte[] encrypted = Crypto.decrypt(decoded,privateKey);
        return new String(encrypted);
    }

    private String encrypt(String string, PublicKey publicKey){
        byte[] encrypted  = Crypto.encrypt(string.getBytes(),publicKey);
        return new String( Base64.encode(encrypted) );
    }
    protected void put(String key, String value){
        data.put(key,encrypt(value,publicKey));
    }

    protected String get(String key){
        return decrypt(data.get(key), privateKey);
    }

    /**
     * EncryptedEntitity Writter, requires the PublicKey of the target for encryption.
     */
    public static class Writter extends EncryptedEntity{

        public Writter(PublicKey publicKey) {
            this.publicKey = publicKey;
        }

        @Override
        public void put(String key, String value){
            super.put(key, value);
        }
    }

    /**
     * EncryptedEntity Reader, requires the matching PrivateKey for decryption.
     */
    public static class Reader extends EncryptedEntity{

        public Reader(PrivateKey privateKey){
            this.privateKey = privateKey;
        }

        public void setKey(PrivateKey privateKey){
            this.privateKey = privateKey;
        }

        @Override
        public String get(String key){
            return super.get(key);
        }

    }

}
