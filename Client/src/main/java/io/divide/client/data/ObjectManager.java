package io.divide.client.data;

import com.google.inject.Inject;
import io.divide.client.BackendObject;
import io.divide.client.BackendUser;
import io.divide.client.cache.LocalStorage;
import io.divide.shared.web.transitory.query.Query;
import io.divide.shared.web.transitory.query.SelectOperation;
import rx.Observable;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by williamwebb on 11/25/13.
 */
public class ObjectManager {

    @Inject DataManager dataManager;
    @Inject LocalStorage localStorage;

    public ObjectManager(){ }

    public LocalStorage local(){
        return localStorage;
    }

    public RemoteStorage remote(){
        return remote;
    }

    private RemoteStorage remote = new RemoteStorage();
    public class RemoteStorage{

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
            if (!query.getFrom().equals(Query.safeTable(type))){
                throw new IllegalStateException("Can not return a different type then what is queried!\n" +
                        "Expected: " + query.getFrom() + "\n" +
                        "Actual: " + Query.safeTable(type));
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
