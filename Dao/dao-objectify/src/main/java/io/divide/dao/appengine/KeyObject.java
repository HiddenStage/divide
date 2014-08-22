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
