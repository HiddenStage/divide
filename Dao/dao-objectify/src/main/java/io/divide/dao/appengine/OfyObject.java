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

import com.googlecode.objectify.annotation.EmbedMap;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.HashMap;
import java.util.Map;

@Entity
public class OfyObject {

    @Id
    public String object_key;

    @Index
    @EmbedMap
    public Map<String,Object> user_data = new HashMap<String, Object>(0);

    @Index
    @EmbedMap
    public Map<String,String> meta_data = new HashMap<String, String>(0);

    public OfyObject(){}

    public OfyObject(String key,Map<String,Object> userData, Map<String,String> metaData){
        this.object_key = key;
        this.user_data = userData;
        this.meta_data = metaData;
    }

    @Override
    public String toString() {
        return "OfyObject{" +
                "object_key='" + object_key + '\'' +
                ", user_data=" + user_data +
                ", meta_data=" + meta_data +
                '}';
    }
}
