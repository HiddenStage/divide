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

package io.divide.dao.appengine;

import com.google.gson.Gson;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class KeyObject {

    private static final Gson gson = new Gson();
    @Id
    private long id = 1;


    String pubKey;
    String priKey;

    public KeyObject(){}
    public KeyObject(byte[] pubKey, byte[] priKey){
        setKeys(pubKey,priKey);
    }

    public void setKeys(byte[] pubKey, byte[] priKey){
        this.pubKey = gson.toJson(pubKey,byte[].class);
        this.priKey = gson.toJson(priKey,byte[].class);
    }

    public byte[] getPublicKey(){
        if(pubKey!=null) return gson.fromJson(pubKey,byte[].class);
        else return null;
    }

    public byte[] getPrivateKey(){
        if(priKey!=null) return gson.fromJson(priKey,byte[].class);
        else return null;
    }
}
