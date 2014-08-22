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

package io.divide.shared.file;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.divide.shared.util.IOUtils;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class XmlUtils
{
    static Gson gson = new GsonBuilder().create();
    static Type listType = new TypeToken<List<ValueContainer>>(){}.getType();
    static Type stringSetType = new TypeToken<Set<String>>(){}.getType();


    public static void writeMapXml(Map<String, ?> mapToWriteToDisk, OutputStream str) throws IOException {

        List<ValueContainer> values = new ArrayList<ValueContainer>(mapToWriteToDisk.size());
        for(Map.Entry<String, ?> object : mapToWriteToDisk.entrySet()){
            String key = object.getKey();
            Object value = object.getValue();
            values.add(new ValueContainer(key,value));
        }

        str.write(gson.toJson(values,listType).getBytes());
    }

    public static Map readMapXml(InputStream str) {
        try {
            Map<String, Object> map = new HashMap<String, Object>();
            List<ValueContainer> values = gson.fromJson(IOUtils.toString(str),listType);
            for(ValueContainer value : values){
                map.put(value.getKey(),value.getValue());
            }
            return map;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private static class ValueContainer{
        public static enum Types{
            Int,
            Long,
            Float,
            Boolean,
            String,
            StringSet;

            public static Types getType(Object o){
                if(o == null) return null;

                if(int.class.equals(o.getClass()) || Integer.class.equals(o.getClass())) return Int;
                if(long.class.equals(o.getClass()) || Long.class.equals(o.getClass())) return Long;
                if(float.class.equals(o.getClass()) || Float.class.equals(o.getClass())) return Float;
                if(boolean.class.equals(o.getClass()) || Boolean.class.equals(o.getClass())) return Boolean;
                if(String.class.equals(o.getClass())) return String;
                if(Set.class.isAssignableFrom(o.getClass())) return StringSet;

                return null;
            }
        }

        private Types type;
        private String key;
        private String value;

        public ValueContainer(String key, Object value){
            this.type = Types.getType(value);
            this.key = key;
            this.value = gson.toJson(value);
        }

        public String getKey(){
            return key;
        }

        public Object getValue(){
            switch (type){
                case Int: return gson.fromJson(value, Integer.class);
                case Long:  return gson.fromJson(value, Long.class);
                case Float: return gson.fromJson(value, Float.class);
                case Boolean: return gson.fromJson(value, Boolean.class);
                case String: return gson.fromJson(value, String.class);
                case StringSet: return gson.fromJson(value, stringSetType);
            }
            return null;
        }
    }
}