package io.divide.client.cache;

import com.google.inject.Inject;
import iBoxDB.LocalServer.AutoBox;
import iBoxDB.LocalServer.DB;
import iBoxDB.LocalServer.E.CommitExpection;
import iBoxDB.LocalServer.IFunction;
import io.divide.client.Config;
import io.divide.shared.server.DAO;
import io.divide.shared.transitory.TransientObject;
import io.divide.shared.transitory.query.Clause;
import io.divide.shared.transitory.query.OPERAND;
import io.divide.shared.transitory.query.Query;
import io.divide.shared.util.ObjectUtils;

import java.io.File;
import java.util.*;

import static io.divide.shared.util.ObjectUtils.isArray;

/**
 * Created by williamwebb on 11/2/13.
 */

public class LocalStorageIBoxDb<T1 extends TransientObject,T2 extends TransientObject> implements DAO<T1,T2> {

    //#from <table> where <condition> order by <field1> desc,<field2> limit <0,-1>  #Condition  == != < <= > >= & | #IFunction=[F1,F2,F3]

    DB db;
    AutoBox box;

    @Inject
    public LocalStorageIBoxDb(Config config){
        this(config.fileSavePath + "dbs");
    }

    public LocalStorageIBoxDb(String path){
        System.out.println("iBox: " + path);
        File f = new File(path);
        f.mkdirs();

        db = new DB(path);
//        db.getConfig().ensureTable("Wrapper", Wrapper.class, "Key", "Table");
        db.ensureTable(Wrapper.class, "Wrapper", "Key", "Table");
        box = db.open();
    }

    @Override
    public void save(T1... objects) throws DAOException {
        for(TransientObject b : objects){
            try {
                boolean exists = exists(b);
                System.out.println("Exists: " + exists);
                if(exists) {
                    System.out.println("update: "+ b.getObjectKey());
                    box.update("Wrapper", new Wrapper(b));
                } else {
                    System.out.println("insert: " + b.getObjectKey());
                    box.insert("Wrapper", new Wrapper(b));
                }
            } catch (CommitExpection e){
                e.printStackTrace();
                throw new DAOException(e);
            }
        }
    }

    @Override
    public void delete(T1... objects) throws DAOException {
        String[] keys = new String[objects.length];
        for(int x=0;x<objects.length;x++){
            keys[x]=objects[x].getObjectKey();
        }

        Iterable<Wrapper> x = internalGet(objects[0].getObjectType(), keys);
        for(Wrapper wr : x){
            box.delete("Wrapper", wr);
        }

//        String[] keys = new String[objects.length];
//        for(int x=0;x<objects.length;x++){
//            keys[x]=objects[x].getObjectKey();
//        }
//
//        String table = objects[0].getObjectType();
//
//        Iterable<Wrapper> toDelete = internalGet(table, keys);
//
//        box.delete("Wrapper", ObjectUtils.toArray(Wrapper.class, toDelete));
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
            type = (Class<B>) Class.forName(Query.reverseTable(query.getFrom()));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        String table = query.getFrom();
        Map<Integer,Clause> where = query.getWhere();

        List<Object> args = new ArrayList<Object>(); args.add(table);
        StringBuilder sb = new StringBuilder();
        sb.append("from Wrapper where Table==? ");
        if(where.size()>0)
        {
            sb.append("&& (");
            for(Clause c : where.values()){
//                System.out.println(c);
                if(c.getPreOperator() != null && c.getPreOperator().length() != 0){
//                    System.out.println("Conditional: " + c.getPreOperator());
                    OPERAND.Conditional con = OPERAND.Conditional.from(c.getPreOperator());
//                    System.out.println("Conditional: " + con);
                    String conString="";
                    switch (con){
                        case AND: conString = "||"; break;
                        case OR: conString = "&&"; break;
                    }
                    sb.append(" ").append(conString).append(" [").append(c.getBefore()).append("]");
                }
                else
                    sb.append(" [").append(c.getBefore()).append("]");
                args.add(new QueryArray(c.getOperand(),c.getAfter()));
            }

//            for(Clause c : where.values()){
//                    sb
//                    .append(" ")
//                    .append(c.getPreOperator())
//                    .append(" ")
//                    .append(c.getBefore())
//                    .append(" ")
//                    .append(c.getOperand())
//                    .append(" ")
//                    .append(c.getAfter());
//            }

            sb.append(')');
        }
        if(query.getLimit() != null){
            sb.append(" limit " + "0," + query.getLimit());
        }

//        System.out.println("Query: " + sb.toString());
//        System.out.println("Args: " + args);

        Iterable<Wrapper> list = box.select(Wrapper.class,sb.toString(),args.toArray());
//        System.out.println("Found: " + list);
        List<B> bList = new ArrayList<B>();
        for(Wrapper w : list){
//            System.out.println(w);
            bList.add(w.toObject(type));
        }
        return bList;
    }

    @Override
    public <O extends T2> Collection<O> get(String type, String... keys) throws DAOException {
        System.out.println("get("+type+"): " + ObjectUtils.v2c(keys));

        Class<O> clazz = null;
        try {
            clazz = (Class<O>) Class.forName(Query.reverseTable(type));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        List<O> list = new ArrayList<O>();
        for(Wrapper w : internalGet(type,keys)) {
            list.add(w.toObject(clazz));
        }

        return list;
    }

    private Iterable<Wrapper> internalGet(String type, String... keys){
        StringBuilder query = new StringBuilder();
        query.append("from Wrapper where Table==? && (");
        List<String> args = new ArrayList<String>(); args.add(type);
        for(int x=0;x<keys.length;x++){
            args.add(keys[x]);
            query.append("Key==?");
            if(x<keys.length - 1) query.append(" || ");
        }
        query.append(')');

        Iterable<Wrapper> x = box.select(Wrapper.class, query.toString(), args.toArray());
        return x;
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
        private OPERAND operand;
        private String match;

        public QueryArray(String operand, String match) {
            this.operand = OPERAND.from(operand);
//            System.out.println("Operand: " + operand);
            this.match = match;
        }

        public Object execute(int argCount, Object[] args) {
//            System.out.println("execute: " + Arrays.asList(args));
            if(compare(args[0])) return true; // since we use this for all queries, check against the match first, then check against embedded arrays
            if(!isArray(args[0])) return false;

            if (args[0] == null) {
                return false;
            }
            Object[] tags = (Object[]) args[0];
            for (Object t : tags) {
//                System.out.println("Tag: " + t);
                if(compare(t))return true;
            }
            return false;
        }

        private boolean compare(Object one){
            int result = match.compareToIgnoreCase(String.valueOf(one));
//            System.out.println(one + " " + operand + " " + match + ": " + result);
            switch (operand){
                case CONTAINS:
                case EQ: return result == 0;
                case GREATER_THAN: return result < 0;
                case GREATER_THAN_EQ: return (result < 0 || result == 0);
                case LESS_THAN: return result > 0;
                case LESS_THAN_EQ: return (result > 0 || result == 0);
            }
            return false;
        }

//        public Object execute(int argCount, Object[] args) {
//            return recursiveCheck(args,match);
//        }
//
//        private static boolean recursiveCheck(Object object, Object target){
//            if(object == null) return false;
//            if(object.equals(target)) return true;
//            if(!isArray(object))return false;
//            else {
//                Object[] tags = (Object[]) object;
//                for(Object o : tags){
//                    if(recursiveCheck(o,target)) return true;
//                }
//            }
//            return false;
//        }

        @Override
        public String toString() {
            return "QueryArray{" +
                    "match" + operand + match + '\'' +
                    '}';
        }
    }

}
