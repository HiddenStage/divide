package com.jug6ernaut.network.authenticator.client.data;

import com.jug6ernaut.network.authenticator.client.*;
import com.jug6ernaut.network.authenticator.client.cache.LocalStorage;
import com.jug6ernaut.network.authenticator.client.cache.LocalStorageNoSQL;
import com.jug6ernaut.network.shared.web.transitory.query.Query;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by williamwebb on 11/25/13.
 */
public class ObjectManager {

    private static DataManager dataManager;
    private static LocalStorage localStorage;

    public ObjectManager(Backend backend){
        dataManager = backend.getDataManager();
        localStorage = new LocalStorageNoSQL(backend.app);
    }

    public LocalStorage local(){
        return localStorage;
    }

    public RemoteStorage remote(){
        return remote;
    }

    private static RemoteStorage remote = new RemoteStorage();
    public static class RemoteStorage{

        private RemoteStorage(){};

        private <B extends BackendObject> Collection<B> assignUser(BackendUser user, B... objects) throws NotLoggedInException {
            return assignUser(user, Arrays.asList(objects));
        }

        private <B extends BackendObject> Collection<B> assignUser(BackendUser user,Collection<B> objects) throws NotLoggedInException{
            for(BackendObject o : objects){
                if(o.getOwnerId() == null){
                    o.setOwnerId(user.getOwnerId());
                }
            }
            return objects;
        }

        public <B extends BackendObject> Saver<B> save(B... objects) throws NotLoggedInException {
            BackendUser user;
            if((user = BackendUser.getUser()) == null) throw new NotLoggedInException();

            return new Saver<B>(dataManager,assignUser(user, objects));
        }

        public <B extends BackendObject> Loader<B> load(Class<B> type, String... keys){
            return new Loader<B>(dataManager,type,keys);
        }

        public <B extends BackendObject> Querier<B> query(Class<B> type, Query<B> query){
            return new Querier<B>(dataManager,type,query);
        }

//        public <B extends BackendObject> void save(B... objects){
//            this.save(checkAndAssignUser(objects));
//        }
//
//        public <B extends BackendObject> void save(Collection<B> objects){
//            dataManager.send(checkAndAssignUser(objects));
//        }
//
//        public <B extends BackendObject> void save(Collection<B> objects, retrofit.Callback<String> callback){
//            dataManager.send(checkAndAssignUser(objects),callback);
//        }
//
//        public <B extends BackendObject> Collection<B> get(Class<B> type, Collection<String> keys){
//            return dataManager.get(type, keys);
//        }
//
//        public <B extends BackendObject> void get(Class<B> type, Collection<String> keys, retrofit.Callback<Collection<B>> callback){
//            dataManager.get(type, keys, callback);
//        }
//
//        public <B extends BackendObject> Collection<B> query(Class<B> type, Query query){
//            return dataManager.query(type,query);
//        }
//
//        public <B extends BackendObject> void query(Class<B> type, Query<B> query, retrofit.Callback<Collection<B>> callback){
//            dataManager.query(type,query,callback);
//        }

//        public <B extends BackendObject> Collection<B> queryAndCache(Class<B> type, Query query){
//            Collection<B> results = dataManager.query(type,query);
//            localStorage.save(results);
//            return results;
//        }
//
//        public <B extends BackendObject> void queryAndCache(Class<B> type, Query query,final retrofit.Callback<Collection<B>> callback){
//            retrofit.Callback<Collection<B>> aCallback = new retrofit.Callback<Collection<B>>(){
//                @Override
//                public void success(Collection<B> objects, Response response) {
//                    localStorage.save(objects);
//                    callback.success(objects,response);
//                }
//
//                @Override
//                public void failure(RetrofitError retrofitError) {
//                    callback.failure(retrofitError);
//                }
//            };
//
//            dataManager.query(type,query,aCallback);
//        }

    }

    public static class NotLoggedInException extends Exception {
    }
}
