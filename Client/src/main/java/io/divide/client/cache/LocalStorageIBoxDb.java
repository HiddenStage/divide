package io.divide.client.cache;

import android.content.Context;
import iBoxDB.LocalServer.AutoBox;
import iBoxDB.LocalServer.DB;
import iBoxDB.LocalServer.E.CommitExpection;
import iBoxDB.LocalServer.IFunction;
import io.divide.shared.server.DAO;
import io.divide.shared.web.transitory.TransientObject;
import io.divide.shared.web.transitory.query.Clause;
import io.divide.shared.web.transitory.query.Query;

import java.util.*;

import static io.divide.shared.util.ObjectUtils.isArray;

/**
 * Created by williamwebb on 11/2/13.
 */

public class LocalStorageIBoxDb<T1 extends TransientObject,T2 extends TransientObject> implements DAO<T1,T2> {

    DB db;
    AutoBox box;

//    @Inject
    public LocalStorageIBoxDb(Context context){
        this(context.getFilesDir().getPath());
    }

    public LocalStorageIBoxDb(String path){
        db = new DB(path);
        db.ensureTable(Wrapper.class, "Wrapper","Key","Table");
        box = db.open();
    }

    public LocalStorageIBoxDb(){
        db = new DB();
        db.ensureTable(Wrapper.class, "Wrapper","Key","Table");
        box = db.open();
    }

    @Override
    public void save(TransientObject... objects) throws DAOException {
        for(TransientObject b : objects){
            try {
                if(exists(b)) {
                    System.out.println("update: "+ b);
                    box.update("Wrapper", new Wrapper(b));
                } else {
                    System.out.println("insert: "+ b);
                    box.insert("Wrapper", new Wrapper(b));
                }
            } catch (CommitExpection e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void delete(TransientObject... objects) throws DAOException {

    }

    @Override
    public boolean exists(TransientObject... objects) {
        if(objects.length == 0) return false;

        boolean found = true;
        for(TransientObject o : objects){
            Wrapper o1 = iBoxUtils.GetFrist(box.select(Wrapper.class,"from Wrapper where Key==? && Table==?", o.getObjectKey(), o.getObjectType() ));

            if(o1 == null) found = false;
        }

        return found;
    }

    @Override
    public int count(String type) {
        return (int) box.selectCount("from Wrapper where Table==?", type);
    }

    @Override
    public <B extends T2> List<B> query(Query query) {

        Class<B> type = null;
        try {
            type = (Class<B>) Class.forName(query.getFrom().replace("_","."));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        String table = query.getFrom();
        Map<Integer,Clause> where = query.getWhere();

        List<Object> args = new ArrayList<Object>();
        StringBuilder sb = new StringBuilder();
        sb.append("from Wrapper where Table==?");args.add(table);
        if(where.size()>0)sb.append(" &&");
        for(Clause c : where.values()){
            if(c.getPreOperator() != null)
                sb.append(" ").append(c.getPreOperator()).append(" ").append(c.getBefore());
            else
                sb.append(" [").append(c.getBefore()).append("]");
            args.add(new QueryArray(c.getAfter()));
        }

        System.out.println("Query: " + sb.toString() + " " + Arrays.asList(args));

        Iterable<Wrapper> list = box.select(Wrapper.class,sb.toString(),args.toArray(new Object[0]));
        System.out.println("Found: " + list);
        List<B> bList = new ArrayList<B>();
        for(Wrapper w : list){
            System.out.println(w);
            bList.add(w.toObject(type));
        }
        return bList;
    }

    @Override
    public <O extends T2> Collection<O> get(String type, String... keys) throws DAOException {
        return null;
    }

//    @Override
//    public <B extends TransientObject> List<B> getAllByType(Class<B> type) {
//        Iterable<Wrapper> list = box.select(Wrapper.class, "from Wrapper where Table==?",Query.safeTable(type));
//        List<B> bList = new ArrayList<B>();
//        for(Wrapper w : list){
//            bList.add(w.toObject(type));
//        }
//        return bList;
//    }

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

        @Override
        public String toString() {
            return "QueryArray{" +
                    "match='" + match + '\'' +
                    '}';
        }
    }

}
