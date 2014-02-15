//package com.jug6ernaut.network.authenticator.client.data;
//
//import com.jug6ernaut.network.authenticator.client.BackendObject;
//import com.jug6ernaut.network.authenticator.client.EmptyCallback;
//import com.jug6ernaut.network.authenticator.client.data.DataManager;
//import com.jug6ernaut.network.shared.util.ObjectUtils;
//import com.jug6ernaut.network.shared.web.transitory.query.Query;
//
//import java.util.Collection;
//
///**
// * Created by williamwebb on 11/25/13.
// */
//public class Querier<B extends BackendObject> {
//
//    private DataManager dataManager;
//    private Class<B> type;
//    private Query<B> query;
//
//    protected Querier(DataManager dataManager, Class<B> type, Query<B> query) {
//        this.dataManager = dataManager;
//        this.type = type;
//        this.query = query;
//    }
//
//    public Collection<B> now() {
//        return dataManager.query(type,query);
//    }
//
//    public void async(retrofit.Callback<Collection<B>>... callbacks) {
//        int length = callbacks.length;
//        retrofit.Callback<Collection<B>> callback;
//        if(callbacks == null || callbacks.length == 0){
//            callback = new EmptyCallback<Collection<B>>();
//        } else
//        if(length == 1){
//            callback = ObjectUtils.get1stOrNull(callbacks);
//        } else {
//            throw new RuntimeException("Only 1 callback allowed");
//        }
//
//        dataManager.query(type,query,callback);
//    }
//
//}
