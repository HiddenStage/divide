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
