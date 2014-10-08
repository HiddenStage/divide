/*
 * Copyright (C) 2014 Divide.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
