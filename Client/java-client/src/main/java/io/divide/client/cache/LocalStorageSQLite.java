//package io.divide.client.cache;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.DatabaseUtils;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.jug6ernaut.android.logging.Logger;
//import io.divide.client.BackendObject;
//import io.divide.shared.server.DAO;
//import io.divide.shared.util.ReflectionUtils;
//import io.divide.shared.web.transitory.TransientObject;
//import io.divide.shared.web.transitory.query.Clause;
//import io.divide.shared.web.transitory.query.Query;
//
//import java.io.IOException;
//import java.util.*;
//
///**
// * Created with IntelliJ IDEA.
// * User: williamwebb
// * Date: 10/2/13
// * Time: 5:56 PM
// *
// * Class takes a transient object, makes a new table for each object type, creating columns for each field, for now
// * all string type, maybe except for known fields.
// */
//
//public class LocalStorageSQLite implements DAO{
//
//    private static final Gson GSON = new GsonBuilder().create();
//    Map<String,DatabaseUtils.InsertHelper> ihMap = new HashMap<String, DatabaseUtils.InsertHelper>();
//
//    Logger logger = Logger.getLogger(LocalStorageSQLite.class);
//    SQLiteUtils sqlUtils;
//    SQLiteDatabase db;
//    Context context;
//
//    protected static final String USER_TAG = TransientObject.USER_DATA+"_";
//    protected static final String META_TAG = TransientObject.META_DATA+"_";
//
//    public LocalStorageSQLite(Context context) throws IOException {
//        this.context = context;
//        DatabaseInitializer di = new DatabaseInitializer(context);
//        initialize(context,di);
//    }
//
//    public void setDBHelper(SQLiteOpenHelper helper){
//        initialize(context,helper);
//    }
//
//    private void initialize(Context context, SQLiteOpenHelper helper){
//        db = helper.getWritableDatabase();
//        sqlUtils = new SQLiteUtils(db);
//    }
//
//    private void setIH(String tableName){
//        ihMap.put(tableName,newIH(tableName));
//    }
//
//    private DatabaseUtils.InsertHelper newIH(String tableName){
//        DatabaseUtils.InsertHelper ih = new DatabaseUtils.InsertHelper(db,tableName);
//        return ih;
//    }
//
//    private DatabaseUtils.InsertHelper getIH(String tableName){
//        DatabaseUtils.InsertHelper ih = ihMap.get(tableName);
//        if(ih == null){
//            ih = newIH(tableName);
//        }
//        return ih;
//    }
//
//    private Set<String> maskColumns(Set<String> user, Set<String> meta){
//        HashSet<String> set = new HashSet<String>(user.size() + meta.size());
//        for(String s : user){
//            set.add(USER_TAG+s);
//        }
//        for(String s : meta){
//            set.add(META_TAG+s);
//        }
//        return set;
//    }
//
//    private <T extends BackendObject> void checkTableSetup(Collection<T> objects){
//        boolean inTransaction = false;
//        int count = 0;
//        int size = objects.size();
//
//        for (T o : objects){
//            if(count++%100 == 0 ){
//                if(inTransaction){
//                    db.setTransactionSuccessful();
//                    db.endTransaction();
//                }
//                db.beginTransaction();
//            }
//
//            String tableName = getCodedTableName(o.getClass());
//            Map user_data = o.getUserData();
//            Map meta_data = o.getMetaData();
//
//            Set newColumns = maskColumns(user_data.keySet(),meta_data.keySet());
//
//            if(!sqlUtils.tableExist(tableName)){
//                sqlUtils.createTable(tableName,newColumns);
//                setIH(tableName);
//            } else {
//                // update table
//                List<String> currentTableColumns = sqlUtils.getColumns(tableName);
//                newColumns.removeAll(currentTableColumns);
//
//                if(!newColumns.isEmpty()){
//                    logger.debug("Update Columns: " + newColumns);
//                    sqlUtils.addColumns(tableName,newColumns);
//                    setIH(tableName);
//                }
//            }
//
//            insertValues(getIH(tableName), tableName, user_data, meta_data);
//
//            if(count == size){
//                db.setTransactionSuccessful();
//                db.endTransaction();
//            }
//        }
//    }
//
//    private static boolean hasUserData = false;
//
//    private void insertValues(DatabaseUtils.InsertHelper ih, String tableName, Map<String, Object> user_data, Map<String,String> meta_data){
//
//        ContentValues metaValues = new ContentValues(meta_data.size());
//        ContentValues userValues = new ContentValues(user_data.size());
//
//        String objectKey = meta_data.get("object_key");
//
//        //insert meta_data
//        for(Map.Entry<String,String> entry : meta_data.entrySet()){
//            String key = META_TAG + entry.getKey();
//            String value = entry.getValue();
//
//            if(value != null)
//                metaValues.put(key,value);
//        }
//
//        if(!exists(tableName,objectKey)){
//            logger.debug("db.insert: " + tableName + " " + meta_data);
//            ih.insert(metaValues);
//        } else {
//            logger.debug("db.update: " + tableName + " " + meta_data);
//            db.update(tableName, metaValues, META_TAG + "object_key=?", new String[]{objectKey});
//        }
//
//        hasUserData = false;
//        //insert user_data
//        if(!user_data.isEmpty()){
//            for(Map.Entry<String,Object> entry : user_data.entrySet()){
//                String key = USER_TAG + entry.getKey();
//                Object value = entry.getValue();
//
//                if(value != null){
//                    hasUserData = true;
//
//                    userValues.put(key, String.valueOf(value));
//
//                    //do not support querying embedded collections yet...
//                    //recursiveCollectionSave(userValues,key, value);
//                }
//            }
//
//            logger.debug("db.update: " + tableName + " " + userValues);
//            db.update(tableName,userValues,META_TAG+"object_key=?",new String[]{objectKey});
//        }
//    }
//
//    private boolean isCollection(Object o){
//        return Collection.class.isAssignableFrom(o.getClass());
//    }
//
//    private boolean isMap(Object o){
//        return Map.class.isAssignableFrom(o.getClass());
//    }
//
//    private void recursiveCollectionSave(ContentValues values, String currentPath, Object object){
//        if(isMap(object)){
//            Map m = (Map)object;
//            Set<Map.Entry> set = m.entrySet();
//            for(Map.Entry e : set){
//                recursiveCollectionSave(values, currentPath+"_"+e.getKey(), e.getValue());
//            }
//        }
//        else if(isCollection(object)){
//            for (Object o : (Collection)object){
//                recursiveCollectionSave(values, currentPath, o);
//            }
//        } else {
//            values.put(currentPath,object.toString());
//        }
//    }
//
//    public <B extends BackendObject> void save(Collection<B> object){
//        db.beginTransaction();
//        checkTableSetup(object);
//        db.setTransactionSuccessful();
//        db.endTransaction();
//    }
//
//    private <B extends BackendObject> boolean exists(String tableName, String objectKey) {
//        try{
//            return DatabaseUtils.queryNumEntries(db, tableName, META_TAG+"object_key=?", new String[]{objectKey}) > 0;
//        }catch (Exception e){
//            logger.error("exists",e);
//            return false;
//        }
//    }
//
//    @Override
//    public <B extends BackendObject> boolean exists(Class<B> type, String objectKey) {
//        String tableName = getCodedTableName(type);
//        return exists(tableName,objectKey);
//    }
//
//    @Override
//    public <B extends BackendObject> long count(Class<B> type) {
//        logger.debug("Count: " + type);
//        String tableName = getCodedTableName(type);
//        try{
//            return DatabaseUtils.queryNumEntries(db, tableName, "", new String[]{});
//        }catch (Exception e){
//            logger.error("exists",e);
//            return -1;
//        }
//    }
//
////    public boolean exists(String tableName,String objectKey){
////        return count(tableName,objectKey) > 0;
////    }
////
////    public long count(String tableName, String objectKey){
////        logger.debug("Key: " + objectKey);
////        return  DatabaseUtils.queryNumEntries(db, tableName, "object_key=?", new String[]{objectKey});
////    }
//
//    public <B extends BackendObject> List<B> getAllByType(Class<B> type){
//        List<B> items = new ArrayList<B>();
//        Cursor c = null;
//        try{
//            String tablename = getCodedTableName(type);
//
//            List<String> columns = sqlUtils.getColumns(tablename);
//            //        Cursor c = db.rawQuery("select " + TextUtils.join(",",columns) + " from " + tablename,null);
//            c = db.query(tablename,columns.toArray(new String[0]),null,null,null,null,null);
//
//            while(c.moveToNext()){
//                items.add(getFromCursor(type,columns,c));
//            }
//        }catch (Exception e){
//            logger.error("",e);
//        }finally {
//            if(c!=null)
//                c.close();
//        }
//
//        return items;
//    }
//
//    public <B extends BackendObject> List<B> query(Class<B> type, Query query){
//        List<B> items = new ArrayList<B>();
//        Cursor c = null;
//        try{
//            String tablename = getCodedTableName(type);
//
//            query = new LocalQuery(query);
//
//            List<String> columns = sqlUtils.getColumns(tablename);
//            String queryString = query.getSQL();
//            queryString = queryString.replace(query.getFrom(),tablename);
//            logger.debug("query:" + queryString);
//
//            c = db.rawQuery(queryString,null);
////            c = db.query(tablename,columns.toArray(new String[0]),null,null,null,null,null);
//            if(c==null){
//
//            }
//            else
//                while(c.moveToNext()){
//                    items.add(getFromCursor(type,columns,c));
//                }
//        }catch (Exception e){
//            logger.error("",e);
//        }finally {
//            if(c!=null)
//                c.close();
//        }
//
//        return items;
//    }
//
//    private <B extends BackendObject> B getFromCursor(Class<B> type,List<String> columns, Cursor c) throws IllegalAccessException, InstantiationException {
//        try{
//            B b = type.newInstance();
//            Map user_data = (Map) ReflectionUtils.getObjectField(b, TransientObject.USER_DATA);
//            Map meta_data = (Map) ReflectionUtils.getObjectField(b, TransientObject.META_DATA);
//
//            for(String column : columns){
//                switch (column.charAt(0)){
//                    case 'u': user_data.put(column.substring(USER_TAG.length(),column.length()), c.getString(c.getColumnIndex(column))); break;
//                    case 'm': meta_data.put(column.substring(META_TAG.length(),column.length()), c.getString(c.getColumnIndex(column))); break;
//                }
//            }
//            return b;
//        }catch (Exception e){
//            return null;
//        }
//    }
//
//    private String getCodedColumnName(String column){
//        return column.replace('.', '_');
//    }
//
//    // make all possible names safe for sql
//    protected static <T extends BackendObject> String getCodedTableName(Class<T> type){
////        return (Base64.encode(type.getName().getBytes()));
//        return type.getName().replace('.','_');
////        return Hex.encodeHexString(type.getName().getBytes());
//    }
//
//    private static class LocalQuery extends Query{
//
//        public LocalQuery(Query query){
//            this.from = query.getFrom();
//            this.select = query.getSelect();
//            this.action = query.getAction();
//            this.limit = query.getLimit();
//            this.offset = query.getOffset();
//            this.random = query.getRandom();
//            this.where.putAll(query.getWhere());
//            this.getWhere();
//        }
//
//        @Override
//        public Map<Integer, Clause> getWhere()
//        {
//            Iterator it = where.entrySet().iterator();
//            while (it.hasNext()) {
//                Map.Entry<Integer,Clause> pairs = (Map.Entry)it.next();
//                Clause c = pairs.getValue();
//                c.setBefore(c.getBefore().replace('.', '_'));
//            }
//            return where;
//        }
//
//    }
//
//    private static final Set<Class<?>> WRAPPER_TYPES = getWrapperTypes();
//
//    public static boolean isWrapperOrPrimative(Object o)
//    {
//        return WRAPPER_TYPES.contains(o.getClass());
//    }
//
//    private static Set<Class<?>> getWrapperTypes()
//    {
//        Set<Class<?>> ret = new HashSet<Class<?>>();
//        ret.add(Boolean.class);   ret.add(boolean.class);
//        ret.add(Character.class); ret.add(char.class);
//        ret.add(Byte.class);      ret.add(byte.class);
//        ret.add(Short.class);     ret.add(short.class);
//        ret.add(Integer.class);   ret.add(int.class);
//        ret.add(Long.class);      ret.add(long.class);
//        ret.add(Float.class);     ret.add(float.class);
//        ret.add(Double.class);    ret.add(double.class);
//        ret.add(Void.class);      ret.add(void.class);
//        ret.add(String.class);
//        return ret;
//    }
//}
