//package io.divide.dao.ibox;
//
//import com.jug6ernaut.network.dao.DAO;
//import io.divide.shared.web.transitory.TransientObject;
//import io.divide.shared.web.transitory.query.Clause;
//import io.divide.shared.web.transitory.query.Query;
//import iBoxDB.LocalServer.AutoBox;
//import iBoxDB.LocalServer.DB;
//import iBoxDB.LocalServer.E.CommitExpection;
//import iBoxDB.LocalServer.IFunction;
//
//import java.security.KeyPair;
//import java.util.*;
//
//import static io.divide.shared.util.ObjectUtils.isArray;
//
///**
// * Created by williamwebb on 2/15/14.
// */
//public class IBoxDbDao implements DAO {
//    DB db;
//    AutoBox box;
//
//    public IBoxDbDao(DB db){
//        this.db = db;
//        db.ensureTable(Wrapper.class, "Wrapper","Key","Table");
//        box = db.open();
//    }
//
//    public static class iBoxUtils{
//
//        public static <T> T GetFrist(Iterable<T> list) {
//            for (T o : list) {
//                return o;
//            }
//            return null;
//        }
//
//    }
//
//    public static class QueryArray implements IFunction {
//        String match;
//
//        public QueryArray(String match) {
//            this.match = match;
//        }
//
//        public Object execute(int argCount, Object[] args) {
//            if(args[0].equals(match)) return true; // since we use this for all queries, check against the match first, then check against embedded arrays
//            if(!isArray(args[0])) return false;
//
//            Object[] tags = (Object[]) args[0];
//            if (tags == null) {
//                return false;
//            }
//            for (Object t : tags) {
//                if (match.equals(t)) {
//                    return true;
//                }
//            }
//            return false;
//        }
//    }
//
//    @Override
//    public List<TransientObject> query(Query query) throws DAOException {
//
//        String table = query.getFrom();
//        Map<Integer,Clause> where = query.getWhere();
//
//        List<Object> args = new ArrayList<Object>();
//        StringBuilder sb = new StringBuilder();
//        sb.append("from Wrapper where Table==?");args.add(table);
//        if(where.size()>0)sb.append(" &&");
//        for(Clause c : where.values()){
//            if(c.getPreOperator() != null)
//                sb.append(" " + c.getPreOperator() +" " + c.getBefore());
//            else
//                sb.append(" [" + c.getBefore()+"]");
//            args.add(new QueryArray(c.getAfter()));
//        }
//
//        System.out.println("Query: " + sb.toString() + " " + args);
//
//        Iterable<Wrapper> list = box.select(Wrapper.class,sb.toString(),args.toArray(new Object[0]));
//        List<TransientObject> bList = new ArrayList<TransientObject>();
//        for(Wrapper w : list){
//            bList.add(w.toObject(TransientObject.class));
//        }
//        return bList;
//    }
//
//    @Override
//    public Collection<TransientObject> get(String objectType, String... keys) throws DAOException {
//        HashMap<String, Object> results = box.selectKey("Wrapper", keys);
//        List<TransientObject> objects = new ArrayList<TransientObject>();
//        for(Map.Entry<String, Object> o : results.entrySet()){
//            Wrapper w = (Wrapper)o;
//            objects.add(w.toObject(TransientObject.class));
//        }
//        return objects;
//    }
//
//    @Override
//    public void save(TransientObject... objects) throws DAOException {
//        for(TransientObject b : objects){
//            try {
//                if(exists(b))
//                    box.update("Wrapper", new Wrapper(b));
//                else
//                    box.insert("Wrapper", new Wrapper(b));
//            } catch (CommitExpection e){
//                e.printStackTrace();
//            }
//        }
//    }
//
//    @Override
//    public void delete(TransientObject... objects) throws DAOException {
//        List<String> keys = new ArrayList<String>();
//        for(TransientObject o : objects)keys.add(o.getObjectKey());
//
//        box.delete(objects[0].getClass().getName(),keys.toArray());
//    }
//
//    @Override
//    public boolean exists(TransientObject... objects) {
//
//        boolean found = true;
//
//        for(TransientObject o : objects){
//            Wrapper o1 = iBoxUtils.GetFrist(box.select(Wrapper.class,"from Wrapper where Key==? && Table==?", o.getObjectKey(), o.getClass().getName() ));
//            if(o1 != null) found = false;
//        }
//
//        return found;
//    }
//
//    @Override
//    public int count(String objectType) {
//        return (int) box.selectCount("from Wrapper where Table==?",objectType);
//    }
//
//    @Override
//    public KeyPair keys(KeyPair keys) {
//        return null;
//    }
//}
