package com.jug6ernaut.network.dao.orientdb;

import com.google.gson.*;
import com.jug6ernaut.network.shared.util.ObjectUtils;
import com.jug6ernaut.network.shared.util.ReflectionUtils;
import com.jug6ernaut.network.shared.web.transitory.TransientObject;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.lang.reflect.Type;
import java.util.*;

import static com.jug6ernaut.network.shared.util.ObjectUtils.*;

/**
 * Created by williamwebb on 2/14/14.
 */
public class Wrapper extends ODocument {

        private static final boolean DEBUG = false;

        public Wrapper(){}

        public <B extends TransientObject> Wrapper(B b){
            setKey(b.getObjectKey());
            Table(b.getClass().getName());
            recursiveSave("user_data","", b.getUserData());
            recursiveSave("meta_data","", b.getMetaData());
        }

        public String getKey() {
            return (String ) this.field("Key");
        }

        public void setKey(String value) {
//            this.setIdentity(new ORecordId())
            this.field("Key", value);
        }

        public String Table() {
            return (String ) this.field("Table");
        }

        public void Table(String value) {
            this.field("Table", value);
        }

        private FileInfo recursiveSave(String root, String current, Object object){
            String currentPath;
            if(current!=null && current.length()>0){
                currentPath = root + "." + current;
            } else currentPath = root;
            if(DEBUG) System.out.println("recursiveSave: " + currentPath);
            if(isMap(object)){
                Map m = (Map)object;
                Manifest manifest = new Manifest();
                Set<Map.Entry> objects = m.entrySet();
                for(Map.Entry e : objects){
                    FileInfo coded = recursiveSave(currentPath, String.valueOf(e.getKey()), e.getValue());
                    manifest.files.add(coded);
                }
                field("x." + currentPath, manifest.toString());
                if(DEBUG) System.out.println("manifest["+"x." + currentPath+"] " + manifest.toString());
                return new FileInfo('x',null,current);
            }
            else if(isCollection(object)){
                Object[] array = ((Collection) object).toArray(new Object[0]);
                if(DEBUG) System.out.println("SaveC["+currentPath+"=" + array +"]");
                field(currentPath, array);

                return new FileInfo('c',object.getClass(),current);
            }
            else if(isArray(object)){
                Object[] array = (Object[]) object;
                field(currentPath, array);

                return new FileInfo('a',object.getClass(),current);
            }
            else {
                if(DEBUG) System.out.println("Save["+currentPath+"=" + object +"]");
                field(currentPath, object);

                return new FileInfo('n',null,current);
            }
        }

        private void recursiveLoad(String currentPath, Map<String,Object> map){
            if(DEBUG) System.out.println("recursiveLoad: " + currentPath);

            String json = (String) field("x." + currentPath);
            Manifest manifest = Manifest.fromString(json);
            if(DEBUG) System.out.println("Files: " + ObjectUtils.v2c(manifest));

            for(FileInfo file : manifest.files) {
                String path = file.path;
                switch (file.type){
                    case 'x' : {
                        recursiveLoad(currentPath + "." + path, map);
                    }break;
                    case 'a' : {
                        if(DEBUG) System.out.println("Loada["+currentPath+"."+path+"]");
                        Class type = file.clazz;
                        Object[] raw = (Object[]) field(currentPath + "." + path);
                        Object o = type.cast(raw);
                        map.put(path,o);
                    }break;
                    case 'c' : {
                        if(DEBUG) System.out.println("Loadc["+currentPath+"."+path+"]");
                        Class type = file.clazz;
                        Object[] raw = (Object[]) field(currentPath + "." + path);
                        Object o = type.cast( v2c(raw) );
                        map.put(path,o);
                    }break;
                    case 'n' : {
                        if(DEBUG) System.out.println("Loadn["+currentPath+"."+path+"]");
                        Object raw = field(currentPath + "." + path);
                        map.put(path,raw);
                    }
                }
            }
        }

        private String fromArray(Object o){

            return "";
        }

        private String fromCollection(Object o){

            return "";
        }

        public <B extends TransientObject> B toObject(Class<B> type){
            try {
                B b = type.newInstance();
                Map user_data = (Map) ReflectionUtils.getObjectField(b, TransientObject.USER_DATA);
                Map meta_data = (Map) ReflectionUtils.getObjectField(b, TransientObject.META_DATA);
                user_data.clear();
                meta_data.clear();


                recursiveLoad("user_data",user_data);
                recursiveLoad("meta_data",meta_data);

                return b;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        private static final class Manifest{
            private static Gson gson;
            static {
                gson = new GsonBuilder().registerTypeAdapter(Class.class,new ClassTypeConverter()).create();
            }
            public final List<FileInfo> files = new ArrayList<FileInfo>();

            @Override
            public String toString(){
                return gson.toJson(this);
            }

            public static Manifest fromString(String string){
                return gson.fromJson(string,Manifest.class);
            }
        }

        private static final class FileInfo{
            public char type;
            public Class clazz;
            public String path;

            public FileInfo(char type, Class clazz, String path){
                this.type = type;
                this.clazz = clazz;
                this.path = path;
            }
        }

        private static class ClassTypeConverter
                implements JsonSerializer<Class>, JsonDeserializer<Class> {
            @Override
            public JsonElement serialize(Class src, Type srcType, JsonSerializationContext context) {
                return new JsonPrimitive(src.getName());
            }

            @Override
            public Class deserialize(JsonElement json, Type type, JsonDeserializationContext context)
                    throws JsonParseException {
                try {
                    return Class.forName(json.getAsString());
                } catch (ClassNotFoundException e) {
                    return null;
                }
            }
        }
}
