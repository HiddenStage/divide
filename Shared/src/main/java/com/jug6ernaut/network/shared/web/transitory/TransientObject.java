package com.jug6ernaut.network.shared.web.transitory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.*;

import static com.jug6ernaut.network.shared.util.ObjectUtils.*;

/*  *
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 8/20/13
 * Time: 6:38 PM
 *
 * User entered data will
 */

public class TransientObject {
    public static final String USER_DATA = "user_data";
    public static final String META_DATA = "meta_data";

    public static final MetaKey OBJECT_TYPE_KEY   = new MetaKey("object_type");
    public static final MetaKey OWNER_ID_KEY       = new MetaKey("owner_key");
    public static final MetaKey OBJECT_KEY        = new MetaKey("object_key");
    public static final MetaKey CREATE_DATE_KEY   = new MetaKey("create_date_key");
    public static final MetaKey MODIFIED_DATE_KEY = new MetaKey("modified_date_key");
    private static final MetaKey PERMISSIONS_KEY = new MetaKey("permissions_key");

    private static Gson gson = initGson();
    protected Map<String,Object> user_data = new HashMap<String, Object>();
    protected Map<String,String> meta_data = new HashMap<String, String>();

    private boolean isNewObject = false;

    private TransientObject(){}

    protected <T extends TransientObject> TransientObject(Class<T> objectType){
        isNewObject = true;
        meta_put(PERMISSIONS_KEY, new FilePermissions());
//        setOwnerId(getLoggedInUser());
        setObjectType(objectType.getName());
        setObjectKey(UUID.randomUUID().toString());
        updateCreateDate();
    }

    private static Gson initGson(){
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .create();
        return gson;
    }

    protected Credentials getLoggedInUser(){
        return null;
    }

    public boolean isReadable(){
        if(isNewObject)return true;
        FilePermissions fp = getFilePermissions();

        Credentials user = getLoggedInUser();

        if(user!=null && user.isSystemUser()) return true;
        else if(fp.isReadable(FilePermissions.Level.WORLD))return true;
        else if(fp.isReadable(FilePermissions.Level.GROUP) && user!=null && fp.getGroups().contains(user.getUserGroup())) return true;
        else if(fp.isReadable(FilePermissions.Level.OWNER) && user!=null && getOwnerId().equals(user)) return true;
        else return false;
    }

    public boolean isWritable(){
        if(isNewObject)return true;
        FilePermissions fp = getFilePermissions();

        Credentials user = getLoggedInUser();

        if(user!=null && user.isSystemUser()) return true;
        else if(fp.isWritable(FilePermissions.Level.WORLD))return true;
        else if(fp.isWritable(FilePermissions.Level.GROUP) && user!=null && fp.getGroups().contains(user.getUserGroup())) return true;
        else if(fp.isWritable(FilePermissions.Level.OWNER) && user!=null && getOwnerId().equals(user)) return true;
        else return false;
    }

    private void canRead(){
        if(!isReadable()) {
            throw new IllegalAccessError(getObjectType() + " is not readable for " + getLoggedInUser());
        }
    }

    private void canWrite(){
        if(!isWritable()) {
            throw new IllegalAccessError(getObjectType() + " is not writable for " + getLoggedInUser());
        }
    }

    public void setFilePermissions(FilePermissions filePermissions){
        if(isWritable()){
            meta_put(PERMISSIONS_KEY, filePermissions);
        }
    }

    public FilePermissions getFilePermissions(){
        return meta_get(FilePermissions.class, PERMISSIONS_KEY);
    }

    protected final void meta_put(MetaKey key, Object object) {
        canWrite();

        updateModifiedDate();
        if(object instanceof String)
            meta_data.put(key.KEY, (String) object);
        else
            meta_data.put(key.KEY,gson.toJson(object));
    }

    protected final <O> O meta_get(Class<O> clazz, MetaKey key){
        canWrite();

        if(clazz.equals(String.class))
            return (O) meta_data.get(key.KEY);
        else
            return gson.fromJson(meta_data.get(key.KEY),clazz);
    }

    protected final boolean meta_remove(MetaKey key){
        return meta_data.remove(key.KEY) != null;
    }

    public void put(String key, Object object) {
        canWrite();

        updateModifiedDate();
        user_data.put(key,object);

//        if(isCollection(object)){
//            Object[] array = ((Collection) object).toArray(new Object[0]);
//            user_data.put(key, array);
//        }
//        else if(isArray(object)){
//            Object[] array = (Object[]) object;
//            user_data.put(key, array);
//        } else {
//            user_data.put(key,object);
//        }
    }

    public void putAll(Map<? extends String ,? extends Object> map) {
        canWrite();

        updateModifiedDate();
        user_data.putAll(map);
    }

    public <O> O get(Class<O> clazz, String key){
        canWrite();

        return (O) user_data.get(key);

//        if(isCollection(clazz)){
//            return (O) v2c((Object[])user_data.get(key));
//        } else {
//            return (O) user_data.get(key);
//        }
    }

    public final boolean remove(String key){
        canWrite();

        updateModifiedDate();
        return user_data.remove(key) != null;
    }

    public final void removeAll(){
        canWrite();

        updateModifiedDate();
        user_data.clear();
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

    public Map<String, Object> getUserData(){
        canRead();

        return new HashMap<String, Object>(user_data);
//        return Collections.unmodifiableMap(user_data);
    }

    public Map<String,String> getMetaData(){
        canRead();

        return new HashMap<String, String>(meta_data);
//        return Collections.unmodifiableMap(meta_data);
    }

    private void setObjectKey(String key){
        meta_put(OBJECT_KEY, key);
    }

    public String getObjectKey(){
        return meta_get(String.class, OBJECT_KEY);
    }

    private void setCreateDate(long date){
        meta_data.put(CREATE_DATE_KEY.KEY, String.valueOf(date));
    }

    public Long getCreateDate(){
        return meta_get(Long.class, CREATE_DATE_KEY);
    }

    private void setModifiedDate(Long modifiedDate){
        meta_data.put(MODIFIED_DATE_KEY.KEY, String.valueOf(modifiedDate));
    }

    public Long getModifiedDate(){
        return meta_get(Long.class, MODIFIED_DATE_KEY);
    }

    private void setObjectType(String type){
        if(type==null || type.length()==0)throw new NullPointerException("Type can not be null");
        meta_data.put(OBJECT_TYPE_KEY.KEY,type); // dont allow null
    }

    public String getObjectType(){
        return meta_get(String.class, OBJECT_TYPE_KEY);
    }

    // accessable to subclasses and this class, stupid java.
    protected void setOwnerId(Integer id){
        meta_put(OWNER_ID_KEY, id); // dont allow null
    }

    public Integer getOwnerId(){
        return meta_get(Integer.class, OWNER_ID_KEY);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()+"{" + "\n" +
                "user_data=" + user_data + "\n" +
                ", meta_data=" + meta_data + "\n" +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(TransientObject.class.isAssignableFrom(o.getClass()))) return false;

        TransientObject that = (TransientObject) o;

        if (isNewObject != that.isNewObject) return false;
        Set<Map.Entry<String, Object>> entries = user_data.entrySet();
        for(Map.Entry<String, Object> entry : entries){
            if(entry == null || entry.getValue() == null || entry.getKey() == null) return false;
            Class c = entry.getValue().getClass();
            String key = entry.getKey();

            Object one = this.get(c,key);
            Object two = that.get(c,key);

            if(one == null || two == null) return false;

            if(isArray(one)) one = v2c((Object[])one);
            if(isArray(two)) two = v2c((Object[])two);

            if(!one.equals(two))return false;
        }

        if (meta_data != null ? !meta_data.equals(that.meta_data) : that.meta_data != null) return false;
//        if (user_data != null ? !user_data.equals(that.user_data) : that.user_data != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = user_data != null ? user_data.hashCode() : 0;
        result = 31 * result + (meta_data != null ? meta_data.hashCode() : 0);
        result = 31 * result + (isNewObject ? 1 : 0);
        return result;
    }

    public static class MetaKey {
        public String KEY = "";
        public MetaKey(String key){
            this.KEY = key;
        }

        @Override
        public String toString(){
            return KEY;
        }
    }

}
