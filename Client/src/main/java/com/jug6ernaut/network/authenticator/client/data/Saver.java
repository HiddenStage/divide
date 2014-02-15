//package com.jug6ernaut.network.authenticator.client.data;
//
//import com.jug6ernaut.network.authenticator.client.BackendObject;
//import com.jug6ernaut.network.authenticator.client.EmptyCallback;
//import com.jug6ernaut.network.authenticator.client.data.DataManager;
//import com.jug6ernaut.network.shared.util.ObjectUtils;
//import retrofit.Callback;
//
//import java.util.Arrays;
//import java.util.Collection;
//
///**
// * Created by williamwebb on 11/25/13.
// */
//public class Saver<B extends BackendObject> {
//
//    private DataManager dataManager;
//    private Collection<B> data;
//
//    protected Saver(DataManager dataManager, B[] objects) {
//        this(dataManager, Arrays.asList(objects));
//    }
//
//    protected Saver(DataManager dataManager, Collection<B> objects) {
//        this.dataManager = dataManager;
//        this.data = objects;
//    }
//
//    public void now() {
//        dataManager.send(data);
//    }
//
//    public void async(Callback<String>... callbacks) {
//        int length = callbacks.length;
//        retrofit.Callback<String> callback;
//        if(callbacks == null || callbacks.length == 0){
//            callback = new EmptyCallback<String>();
//        } else
//        if(length == 1){
//            callback = ObjectUtils.get1stOrNull(callbacks);
//        } else {
//            throw new RuntimeException("Only 1 callback allowed");
//        }
//
//        dataManager.send(data, callback);
//    }
//}
