package io.divide.client.cache;

import android.content.Context;
import io.divide.client.BackendObject;
import io.divide.shared.web.transitory.query.Clause;
import io.divide.shared.web.transitory.query.Query;
import iBoxDB.LocalServer.AutoBox;
import iBoxDB.LocalServer.DB;
import iBoxDB.LocalServer.E.CommitExpection;
import iBoxDB.LocalServer.IFunction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static io.divide.shared.util.ObjectUtils.isArray;

/**
 * Created by williamwebb on 11/2/13.
 */

public class LocalStorageIBoxDb implements LocalStorage {

    DB db;
    AutoBox box;

    public LocalStorageIBoxDb(Context context){
        db = new DB(1,context.getFilesDir().getPath().toString());
        db.ensureTable(Wrapper.class, "Wrapper","Key","Table");
        box = db.open();
    }

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
        Wrapper o1 = iBoxUtils.GetFrist(box.select(Wrapper.class,"from Wrapper where Key==? && Table==?", objectKey, Query.safeTable(type) ));

        return o1 != null;
    }

    @Override
    public <B extends BackendObject> long count(Class<B> type) {
        return box.selectCount("from Wrapper where Table==?", Query.safeTable(type));
    }

    @Override
    public <B extends BackendObject> List<B> query(Class<B> type, Query query) {

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
        Iterable<Wrapper> list = box.select(Wrapper.class, "from Wrapper where Table==?",Query.safeTable(type));
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

}
