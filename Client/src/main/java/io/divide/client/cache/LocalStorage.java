//package io.divide.client.cache;
//
//import io.divide.shared.web.transitory.query.Query;
//
//import java.util.Collection;
//import java.util.List;
//
///**
// * Created by williamwebb on 11/2/13.
// */
//public interface LocalStorage<Object> {
//
//    public <B extends Object> List<B> query(Class<B> type, Query query);
//    public <B extends Object> List<B> getAllByType(Class<B> type);
//    public <B extends Object> void save(Collection<B> objects);
//    public <B extends Object> boolean exists(Class<B> type,String objectKey);
//    public <B extends Object> long count(Class<B> type);
//}
