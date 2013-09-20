package com.jug6ernaut.network.shared.web.transitory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;

import java.util.*;

/*  *
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 8/20/13
 * Time: 6:38 PM
 */
public abstract class TransientObject {
    public static final String USER_DATA = "user_data";
//    public static final String META_DATA = "meta_data";

    public static final String OJBECT_TYPE_KEY = "object_type";
    public static final String USER_ID_KEY = "user_key";
    public static final String OBJECT_KEY = "object_key";
    public static final String CREATE_DATE_KEY = "create_date_key";
    public static final String MODIFIED_DATE_KEY = "modified_date_key";

    private static final List<String> PROTECTED_KEYS = Arrays.asList(new String[]{
            OJBECT_TYPE_KEY,
            USER_ID_KEY,
            OBJECT_KEY,
            CREATE_DATE_KEY,
            MODIFIED_DATE_KEY
    });

    private static Gson gson = initGson();
    private Map<String,String> user_data = new HashMap<String, String>();
//    private Map<String,String> meta_data = new HashMap<String, String>();

    protected TransientObject(String objectType){
        setObjectType(objectType);
        setObjectKey(UUID.randomUUID().toString());
        updateCreateDate();
    }

    private static Gson initGson(){
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .create();
        return gson;
    }
//    public void put(String key, String string) throws JsonIOException {
//        updateModifiedDate();
//        user_data.put(key,string);
//    }

    public void put(String key, Object object) throws JsonIOException {
        updateModifiedDate();
        key = getKey(key);
        if(object instanceof String)
            user_data.put(key, (String) object);
        else
            user_data.put(key,gson.toJson(object));
    }

    public  <T> T get(Class<T> clazz, String key){
        key = getKey(key);
        if(clazz.equals(String.class))
            return (T) user_data.get(key);
        else
            return gson.fromJson(user_data.get(key),clazz);
    }

    public boolean remove(String key){
        updateModifiedDate();
        return user_data.remove(key) != null;
    }

    private long updateCreateDate(){
        long createDate = System.currentTimeMillis();
        setCreateDate(createDate);
        setModifiedDate(createDate);

        return createDate;
    }

    private long updateModifiedDate(){
        long createDate = System.currentTimeMillis();
        setModifiedDate(createDate);

        return createDate;
    }

    public Map<String, String> getUserData(){
        return user_data;
//        return Collections.unmodifiableMap(user_data);
    }

//    public Map<String,String> getMetaData(){
//        return meta_data;
////        return Collections.unmodifiableMap(meta_data);
//    }

    private void setObjectKey(String key){
        put(OBJECT_KEY, key);
    }

    public String getObjectKey(){
        return get(String.class, OBJECT_KEY);
    }

    private void setCreateDate(long date){
        user_data.put(CREATE_DATE_KEY, String.valueOf(date));
    }

    public Long getCreateDate(){
        return get(Long.class, CREATE_DATE_KEY);
    }

    private void setModifiedDate(Long modifiedDate){
        user_data.put(MODIFIED_DATE_KEY, String.valueOf(modifiedDate));
    }

    public Long getModifiedDate(){
        return get(Long.class, MODIFIED_DATE_KEY);
    }

    private void setObjectType(String type){
        if(type==null || type.length()==0)throw new NullPointerException("Type can not be null");
        user_data.put(OJBECT_TYPE_KEY,type); // dont allow null
//        put(OJBECT_TYPE_KEY, (type == null) ? "" : type); // dont allow null
    }

    public String getObjectType(){
        return get(String.class,OJBECT_TYPE_KEY);
    }

    // accessable to subclasses and this class, stupid java.
    protected void setUserId(String id){
        put(USER_ID_KEY, id); // dont allow null
    }

    public String getUserId(){
        return get(String.class,USER_ID_KEY);
    }

    public final static String getKey(String key){
        return ((PROTECTED_KEYS.contains(key)?key:"~"+key));
    }

    @Override
    public String toString() {
        return "TransientObject{" + "\n" +
                "user_data=" + user_data + "\n" +
//                ", meta_data=" + meta_data + "\n" +
                '}';
    }
}
