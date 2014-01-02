package com.jug6ernaut.network.authenticator.client.cache;

import android.content.Context;
import com.jug6ernaut.network.authenticator.client.BackendObject;
import com.jug6ernaut.network.shared.web.transitory.query.Clause;
import com.jug6ernaut.network.shared.web.transitory.query.Query;
import iBoxDB.LocalServer.AutoBox;
import iBoxDB.LocalServer.DB;
import iBoxDB.LocalServer.E.CommitExpection;
import iBoxDB.LocalServer.IFunction;

import java.util.*;

import static com.jug6ernaut.network.shared.util.ObjectUtils.isArray;

/**
 * Created by williamwebb on 11/2/13.
 */

public class LocalStorageNoSQL implements LocalStorage {

    DB db;
    AutoBox box;
    Map<Class<?>,AutoBox> boxMap = new HashMap<Class<?>, AutoBox>();

    public LocalStorageNoSQL(Context context){
        db = new DB(1,context.getFilesDir().getPath().toString());
        db.ensureTable(Wrapper.class, "Wrapper","Key","Table");
        box = db.open();
    }

//    private <B extends BackendObject> AutoBox getOrCreateBox(Class<B> type){
//        AutoBox box;
//        if(boxMap.containsKey(type)){
//            box = boxMap.get(type);
//        } else {
//
//            db.ensureTable(Wrapper.class,"Table","key");
//            box = db.open();
//            boxMap.put(type,box);
//        }
//        return box;
//    }

    @Override
    public <B extends BackendObject> void save(Collection<B> objects) {
        for(B b : objects){
            try {
                if(exists(b.getClass(),b.getObjectKey()))
                    box.update("Wrapper", new Wrapper(b));
                else
                    box.insert("Wrapper", new Wrapper(b));
            } catch (CommitExpection e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public <B extends BackendObject> boolean exists(Class<B> type, String objectKey) {
        Wrapper o1 = iBoxUtils.GetFrist(box.select(Wrapper.class,"from Wrapper where Key==? && Table==?", objectKey,type.getName() ));

        return o1 != null;
    }

    @Override
    public <B extends BackendObject> long count(Class<B> type) {
        return box.selectCount("from Wrapper where Table==?",type.getName());
    }

    @Override
    public <B extends BackendObject> List<B> query(Class<B> type, Query<B> query) {

        String table = query.getFrom();
        Map<Integer,Clause> where = query.getWhere();

        List<Object> args = new ArrayList<Object>();
        StringBuilder sb = new StringBuilder();
        sb.append("from Wrapper where Table==?");args.add(table);
        if(where.size()>0)sb.append(" &&");
        for(Clause c : where.values()){
            if(c.getPreOperator() != null)
                sb.append(" " + c.getPreOperator() +" " + c.getBefore());
            else
                sb.append(" [" + c.getBefore()+"]");
            args.add(new QueryArray(c.getAfter()));
        }

        System.out.println("Query: " + sb.toString() + " " + args);

        Iterable<Wrapper> list = box.select(Wrapper.class,sb.toString(),args.toArray(new Object[0]));
        List<B> bList = new ArrayList<B>();
        for(Wrapper w : list){
            bList.add(w.toObject(type));
        }
        return bList;
    }

    @Override
    public <B extends BackendObject> List<B> getAllByType(Class<B> type) {
        Iterable<Wrapper> list = box.select(Wrapper.class, "from Wrapper where Table==?",type.getName());
        List<B> bList = new ArrayList<B>();
        for(Wrapper w : list){
            bList.add(w.toObject(type));
        }
        return bList;
    }

    public static class iBoxUtils{

        public static <T> T GetFrist(Iterable<T> list) {
            for (T o : list) {
                return o;
            }
            return null;
        }

        public static String GetDou(double d) {
            long l = (long) (d * 1000);
            return Double.toString(l / 1000.0);
        }

    }

    public static class QueryArray implements IFunction {
        String match;

        public QueryArray(String match) {
            this.match = match;
        }

        public Object execute(int argCount, Object[] args) {
            if(args[0].equals(match)) return true; // since we use this for all queries, check against the match first, then check against embedded arrays
            if(!isArray(args[0])) return false;

            Object[] tags = (Object[]) args[0];
            if (tags == null) {
                return false;
            }
            for (Object t : tags) {
                if (match.equals(t)) {
                    return true;
                }
            }
            return false;
        }
    }

    public static class QueryEmbeddedMap implements IFunction {
        String match;

        public QueryEmbeddedMap(String match) {
            this.match = match;
        }

        public Object execute(int argCount, Object[] args) {
            Map tags = (Map) args[0];
            if (tags == null) {
                return false;
            }
            for(Object e : tags.values()){
                if (match.equals(e)) {
                    return true;
                }
            }
            return false;
        }
    }

}
