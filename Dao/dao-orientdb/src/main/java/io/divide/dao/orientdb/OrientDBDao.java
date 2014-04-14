package io.divide.dao.orientdb;

import io.divide.dao.ServerDAO;
import io.divide.shared.web.transitory.TransientObject;
import io.divide.shared.web.transitory.query.OPERAND;
import io.divide.shared.web.transitory.query.Query;
import io.divide.shared.web.transitory.query.QueryBuilder;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.core.tx.OTransaction;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by williamwebb on 2/14/14.
 */
public class OrientDBDao implements ServerDAO {

    public static final String DEFAULT_CONFIG = "memory:default";

    ODatabaseDocument db;

    public OrientDBDao(){
        this.db = new ODatabaseDocumentTx(DEFAULT_CONFIG);
        if(this.db.exists()){
            db.open("admin","admin");
        } else {
            db.create();
        }
    }

    public OrientDBDao(ODatabaseDocument db){
        this.db = db;
    }

    private void checkDb(){
        ODatabaseRecordThreadLocal.INSTANCE.set(db);
    }

    @Override
    public List<TransientObject> query(Query query) throws DAOException {
        checkDb();
        List<TransientObject> list = new ArrayList<TransientObject>();

        OTransaction transaction = db.getTransaction();
        transaction.begin();
        try{
            String q = query.getSQL();
            System.out.println("OrientDB_Query: " + q);

            if(query.getAction().equals(QueryBuilder.QueryAction.SELECT)){
                List<ODocument> objects = db.query(new OSQLSynchQuery<ODocument>(q));
                    for(ODocument w : objects){
                        list.add( new ODocumentWrapper(w).toObject(TransientObject.class));
                    }
            }
            if(query.getAction().equals(QueryBuilder.QueryAction.DELETE)) {
    //            List<ODocument> objects = db.command(new OCommandSQL("delete from io.divide.dao.TestObject1 RETURN BEFORE")).execute();
                Integer objects = db.command(new OCommandSQL(q)).execute();
                TransientObject o = new EmptyTO();
                o.put("count",objects);
                list.add(o);
                System.out.println("Delete: " + objects);
            }

            transaction.commit();
            transaction.close();
        }catch (Exception e){
            transaction.rollback();
            transaction.close();
        }
        return list;
    }

    @Override
    public Collection<TransientObject> get(String objectType, String... keys) throws DAOException {
        if(keys.length == 0) return Arrays.asList();
//        String objectType = Query.safeTable(type);

        checkDb();
        OTransaction transaction = db.getTransaction();
        transaction.begin();
//        System.err.println(db.browseCluster(objectType).next());
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM " + objectType + " WHERE ");
        for(int x=0;x<keys.length;x++){
            sb.append("meta_data.object_key = '" + keys[x] +"'");
            if(x+1<keys.length) sb.append( " OR ");
        }
        List<TransientObject> objects = new ArrayList<TransientObject>();
        System.err.println("Query: " + sb.toString());
        List<ODocument> list = db.query(new OSQLSynchQuery<ODocument>(sb.toString()));
        System.err.println("Found: " + list.size());
        for(ODocument w : list){
            System.err.println("Get: " + w);
            TransientObject to = ODocumentWrapper.toObject(w, TransientObject.class);
            objects.add(to);
        }
        transaction.commit();
        transaction.close();
        return objects;
    }

    @Override
    public void save(TransientObject... objects) throws DAOException {
        System.out.println("save: " + Arrays.asList(objects));
        checkDb();

        OTransaction transaction = db.getTransaction();
        transaction.begin();
        for(TransientObject t : objects){
            db.save(new ODocumentWrapper(t));
        }
        transaction.commit();
        transaction.close();
    }

    @Override
    public void delete(TransientObject... objects) throws DAOException {
        checkDb();

        if(objects.length == 0) return;

        QueryBuilder.WhereMoreBuilder builder = new QueryBuilder()
                .select()
                .from(objects[0].getClass())
                .where(TransientObject.OBJECT_KEY, OPERAND.EQ, objects[0].getObjectKey());

        for(int x=1;x<objects.length;x++){
            builder.or(TransientObject.OBJECT_KEY, OPERAND.EQ, objects[x].getObjectKey());
        }

        Query q = builder.build();

//        StringBuilder sb = new StringBuilder();
//        sb.append("SELECT * FROM " + objects[0].getClass().getName() + " WHERE ");
//        for(int x=0;x<objects.length;x++){
//            sb.append("meta_data.object_key = '" + objects[x].getObjectKey() +"'");
//            if(x+1<objects.length)
//                sb.append( " OR ");
//        }


        System.out.println("Delete: " + q.getSQL());
        List<ODocument> list = db.query(new OSQLSynchQuery<ODocument>(q.getSQL()));
        for(ODocument w : list){
            System.out.println("Deleting: " + w);
            w.delete();
        }
    }

    @Override
    public boolean exists(TransientObject... objects) {
        checkDb();

        if(objects.length == 0) return false;

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM " + Query.safeTable(objects[0].getClass()) + " WHERE ");
        for(int x=0;x<objects.length;x++){
            sb.append("meta_data.object_key = '" + objects[x].getObjectKey() +"'");
            if(x+1<objects.length)
                sb.append( " OR ");
        }
        List<ODocument> list = db.query(new OSQLSynchQuery<ODocument>(sb.toString()));
        return objects.length == list.size();
    }

    @Override
    public int count(String objectType) {
        checkDb();
        try {
            return (int) db.countClass(objectType);
        }catch (java.lang.IllegalArgumentException e){
            return 0;
        }
    }

    @Override
    public KeyPair keys(KeyPair keys) {
        return null;
    }

    private static class EmptyTO extends TransientObject{

        protected EmptyTO() {
            super(TransientObject.class);
            this.meta_data.clear();
            this.user_data.clear();
        }

        @Override
        public void put(String key, Object value){
            super.put(key,value);
            meta_data.clear();
        }
    }

}
