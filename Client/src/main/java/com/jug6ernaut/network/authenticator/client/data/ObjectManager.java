package com.jug6ernaut.network.authenticator.client.data;

import com.jug6ernaut.network.authenticator.client.*;
import com.jug6ernaut.network.authenticator.client.cache.LocalStorage;
import com.jug6ernaut.network.authenticator.client.cache.LocalStorageIBoxDb;
import com.jug6ernaut.network.shared.web.transitory.query.Query;
import com.jug6ernaut.network.shared.web.transitory.query.SelectOperation;
import rx.Observable;

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
        localStorage = new LocalStorageIBoxDb(backend.app);
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

        public <B extends BackendObject> Observable<String> save(B... objects){
            try{
                BackendUser user;
                if((user = BackendUser.getUser()) == null) throw new NotLoggedInException();

                return dataManager.send(assignUser(user, objects));
            }catch (Exception e){
                return Observable.error(e);
            }
        }

        public <B extends BackendObject> Observable<Collection<B>> load(Class<B> type, String... keys){
            return dataManager.get(type,Arrays.asList(keys));
//            return new Loader(dataManager,type,keys);
        }

        public <B extends BackendObject> Observable<Collection<B>> query(Class<B> type, Query query){
            if(query.getSelect() != null){
                SelectOperation so = query.getSelect();
                if(!so.getType().equals(type))
                    throw new IllegalStateException(so.getErrorMessage());
            }
            if (!query.getFrom().equals(type.getName())){
                throw new IllegalStateException("Can not return a different type then what is queried");
            }
            return dataManager.query(type,query);
//            return new Querier(dataManager,type,query);
        }

        public <B extends BackendObject> Observable<Integer> count(Class<B> type){
            return dataManager.count(type);
        }

    }

    public static class NotLoggedInException extends Exception {
    }
}
