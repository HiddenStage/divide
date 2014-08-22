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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.divide.shared.logging.Logger;
import io.divide.shared.transitory.query.Query;

import java.util.*;

import static io.divide.shared.util.ObjectUtils.isArray;
import static io.divide.shared.util.ObjectUtils.v2c;

/*
 * User entered data will
 */

public class TransientObject {
    private static final Logger logger = Logger.getLogger(TransientObject.class);

    public static final String USER_DATA = "user_data";
    public static final String META_DATA = "meta_data";

    public static final MetaKey OBJECT_TYPE_KEY   = new MetaKey("object_type");
    public static final MetaKey OWNER_ID_KEY       = new MetaKey("owner_key");
    public static final MetaKey OBJECT_KEY        = new MetaKey("object_key");
    public static final MetaKey CREATE_DATE_KEY   = new MetaKey("create_date_key");
    public static final MetaKey MODIFIED_DATE_KEY = new MetaKey("modified_date_key");
    private static final MetaKey PERMISSIONS_KEY = new MetaKey("permissions_key");

    private static Gson gson = new GsonBuilder().serializeNulls().create();
    protected Map<String,Object> user_data = new LinkedHashMap<String, Object>();
    protected Map<String,String> meta_data = new LinkedHashMap<String, String>();

    private transient boolean isNewObject = false;

    @SuppressWarnings("unused")
    private TransientObject(){}

    protected <T extends TransientObject> TransientObject(Class<T> objectType){
        isNewObject = true;
        meta_put(PERMISSIONS_KEY, new FilePermissions());
        setObjectType(Query.safeTable(objectType));
        setObjectKey(UUID.randomUUID().toString());
        updateCreateDate();
    }

    protected Credentials getLoggedInUser(){
        return null;
    }

    /**
     * @return whether this object is readable for the currently logged in user.
     */
    public boolean isReadable(){
        if(isNewObject)return true;
        FilePermissions fp = getFilePermissions();

        Credentials user = getLoggedInUser();

        if(user == null) return false;
        else if(user.isSystemUser()) return true;
        else if(fp.isReadable(FilePermissions.Level.WORLD))return true;
        else if(fp.isReadable(FilePermissions.Level.GROUP) && fp.getGroups().contains(user.getUserGroup())) return true;
        else if(fp.isReadable(FilePermissions.Level.OWNER) && getOwnerId().equals(user.getOwnerId())) return true;
        else return false;
    }

    /**
     * @return whether this object is writable for the currently logged in user.
     */
    public boolean isWritable(){
        if(isNewObject)return true;
        FilePermissions fp = getFilePermissions();

        Credentials user = getLoggedInUser();

        if(user == null) return false;
        else if(user.isSystemUser()) return true;
        else if(fp.isWritable(FilePermissions.Level.WORLD))return true;
        else if(fp.isWritable(FilePermissions.Level.GROUP) && fp.getGroups().contains(user.getUserGroup())) return true;
        else if(fp.isWritable(FilePermissions.Level.OWNER) && getOwnerId().equals(user.getOwnerId())) return true;
        else return false;
    }

    private void canRead(){
        if(!isReadable()) {
            throw new IllegalAccessError(meta_data.get(OBJECT_KEY.KEY) + " is not readable for " + getLoggedInUser() +". " + getFilePermissions());
        }
    }

    private void canWrite(){
        if(!isWritable()) {
            throw new IllegalAccessError(meta_data.get(OBJECT_KEY.KEY) + " is not writable for " + getLoggedInUser() +". " + getFilePermissions());
        }
    }

    /**
     * Sets new file permissions for this object.
     * @param filePermissions file permissions to be used for this object.
     */
    public void setFilePermissions(FilePermissions filePermissions){
        if(isWritable()){
            meta_data.put(PERMISSIONS_KEY.KEY,gson.toJson(filePermissions,FilePermissions.class));
        }
    }

    /**
     * Retrieves the file permissions for this object. This not a direct reference. If changes are made they must be stored
     * with setFilePermissions().
     * @return
     */
    public FilePermissions getFilePermissions(){
        return gson.fromJson(meta_data.get(PERMISSIONS_KEY.KEY), FilePermissions.class);
    }

    protected final void meta_put(MetaKey key, Object object) {
//        canWrite(); TODO disabled for now.

        updateModifiedDate();
        if(object instanceof String)
            meta_data.put(key.KEY, (String) object);
        else
            meta_data.put(key.KEY,gson.toJson(object));
    }

    protected final <O> O meta_get(Class<O> clazz, MetaKey key){
//        canRead(); TODO disabled for now

        if(clazz.equals(String.class))
            return clazz.cast(meta_data.get(key.KEY));
        else
            return gson.fromJson(meta_data.get(key.KEY),clazz);
    }

    protected final boolean meta_remove(MetaKey key){
        return meta_data.remove(key.KEY) != null;
    }

    /**
     * Add a specific key value pair to this object.
     * @param key key to identify this element by.
     * @param object object to be added.
     * @param serialize whether to manually serialize this object and store it as a json blob.
     */
    public void put(String key, Object object, boolean serialize){
        if(serialize)
            put(key,gson.toJson(object));
        else
            put(key,object);
    }

    /**
     * Add a specific key value pair to this object.
     * @param key key to identify this element by.
     * @param object object to be added.
     */
    public void put(String key, Object object) {
//        canWrite() TODO disabled for now;

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

    /**
     * Add a map to this object. Must be of type <String,?>
     * @param map
     */
    public void putAll(Map<? extends String ,?> map) {
//        canWrite(); TODO disabled for now

        updateModifiedDate();
        user_data.putAll(map);
    }

    /**
     * Retrieve a specific element from this object.
     * @param clazz type of object to be retrieved. If type given does not match a classcast exception will be thrown.
     * @param key key of object to be retrieved.
     * @param deserialize specify of whether or not this object needs to be manually deserialized(stored as a json string).
     * @return element of type specified corrosponding to the given key.
     */
    public <O> O get(Class<O> clazz, String key, boolean deserialize){
        if(deserialize)
            return gson.fromJson(get(String.class,key),clazz);
        else
            return get(clazz,key);
    }

    /**
     * Retrieve a specific element from this object.
     * @param clazz type of object to be retrieved. If type given does not match a classcast exception will be thrown.
     * @param key key of object to be retrieved.
     * @return element of type specified corrosponding to the given key.
     */
    public <O> O get(Class<O> clazz, String key){
//        canWrite(); TODO disabled for now
        Object o = null;
        try{
            o = user_data.get(key);
            return clazz.cast( o );
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("Get(" + clazz.getSimpleName() + "," + key + ")");
            System.out.println(o);
            return null;
        }
    }

    /**
     * Remove a specific user data element corrosponding to the given key.
     * @param key key of object to be removed.
     * @return
     */
    public final boolean remove(String key){
//        canWrite(); TODO disabled for now

        updateModifiedDate();
        return user_data.remove(key) != null;
    }

    /**
     * Removes all user data. This can not be undone.
     */
    public final void removeAll(){
//        canWrite(); TODO disabled for now

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

    /**
     * @return map representing this objects total user data. This is a new map, not a direct reference.
     */
    public Map<String, Object> getUserData(){
//        canRead(); TODO disabled for now

        return new HashMap<String, Object>(user_data);
//        return Collections.unmodifiableMap(user_data);
    }

    /**
     * @return map representing this objects total metadata. This is a new map, not a direct reference.
     */
    public Map<String,String> getMetaData(){
//        canRead(); TODO disabled for now

        return new HashMap<String, String>(meta_data);
//        return Collections.unmodifiableMap(meta_data);
    }

    private void setObjectKey(String key){
        meta_put(OBJECT_KEY, key);
    }

    /**
     * @return UUID key for this object.
     */
    public String getObjectKey(){
        return meta_get(String.class, OBJECT_KEY);
    }

    private void setCreateDate(long date){
        meta_data.put(CREATE_DATE_KEY.KEY, String.valueOf(date));
    }

    /**
     * @return this objects creation date.
     */
    public Long getCreateDate(){
        return meta_get(Long.class, CREATE_DATE_KEY);
    }

    private void setModifiedDate(Long modifiedDate){
        meta_data.put(MODIFIED_DATE_KEY.KEY, String.valueOf(modifiedDate));
    }

    /**
     * @return last modified date for this object.
     */
    public Long getModifiedDate(){
        return meta_get(Long.class, MODIFIED_DATE_KEY);
    }

    private void setObjectType(String type){
        if(stringIsEmpty(type))throw new NullPointerException("Type can not be null");
        meta_data.put(OBJECT_TYPE_KEY.KEY,type); // dont allow null
    }

    /**
     * @return this objects type, package + class name.
     */
    public String getObjectType(){
        return meta_get(String.class, OBJECT_TYPE_KEY);
    }

    // accessable to subclasses and this class, stupid java.
    protected void setOwnerId(Integer id){
        if(getOwnerId() != null){
            logger.warn("Attempting to set owner id for an object where it as previously been set. Ignoring new id");
            return;
        }
        meta_put(OWNER_ID_KEY, id); // dont allow null
    }

    /**
     * Owner Id representing the owner id of ther user who created this object
     * @return Owner Id of user who created this object.
     */
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
        if (null == o) return false;
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
        if (user_data != null ? !user_data.equals(that.user_data) : that.user_data != null) return false;

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

    private static boolean stringIsEmpty(String string){
        if(string == null) return true;
        string = string.trim();
        return ( string.length()==0 );

    }

}
