/*
 * Copyright (C) 2014 Divide.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.divide.client.data;

import com.google.gson.Gson;
import com.google.inject.Inject;
import io.divide.client.BackendObject;
import io.divide.client.Config;
import io.divide.client.auth.AuthManager;
import io.divide.client.web.AbstractWebManager;
import io.divide.shared.transitory.TransientObject;
import io.divide.shared.transitory.query.Query;
import io.divide.shared.util.IOUtils;
import io.divide.shared.util.ObjectUtils;
import retrofit.client.Response;
import rx.Observable;
import rx.Subscriber;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Collection;

/*
 * WebManager class used to handle all object transactions between client and server.
 */
public class DataManager extends AbstractWebManager<DataWebService> {

    @Inject private AuthManager authManager;

    @Inject
    public DataManager(Config config) {
        super(config);
    }

    @Override
    protected Class<DataWebService> getType() {
        return DataWebService.class;
    }

    /**
     * Function used to save objects on remote server.
     * @param objects Collection of objects to be saved.
     * @param <B> Object type that extends BackendObject.
     * @return Observable, no entity returned.
     */
    public <B extends BackendObject> Observable<Void> send(final Collection<B> objects){
        return getWebService().save(isLoggedIn(),objects)
               .subscribeOn(config.subscribeOn()).observeOn(config.observeOn());
    }

    /**
     * Function used to return specific objects corrosponding to the object keys provided.
     * @param type Type of objects to be returned, if an object of a key provided does not match Type, it will not be returned.
     * @param objects Collection of keys you wish to return from remote server.
     * @param <B> Class type to be returned, extends BackendObject.
     * @return Collection of objects corrosponding to keys provided.
     */
    public <B extends BackendObject> Observable<Collection<B>> get(final Class<B> type, final Collection<String> objects){
        return Observable.create(new Observable.OnSubscribe<Collection<B>>() {
            @Override
            public void call(Subscriber<? super Collection<B>> observer) {
                try {
                    observer.onNext(convertRequest(getArrayType(type), getWebService().get(isLoggedIn(),Query.safeTable(type), objects)));
                    observer.onCompleted();
                } catch (Exception e) {
                    observer.onError(e);
                }
            }
        }).subscribeOn(config.subscribeOn()).observeOn(config.observeOn());

    }

    /**
     * Function used to perform a remote query.
     * @param type Type of objects to be returned, Type must match that which was provided to the Query.
     * @param query Query to be executed.
     * @param <B> Type of object extending BackendObject to be returned.
     * @return Collection of objects matching query executed.
     */
    public <B extends BackendObject> Observable<Collection<B>> query(final Class<B> type,final Query query){
        return Observable.create(new Observable.OnSubscribe<Collection<B>>() {
            @Override
            public void call(Subscriber<? super Collection<B>> observer) {
                try {
                    observer.onNext(convertRequest(getArrayType(type),getWebService().query(isLoggedIn(),query)));
                    observer.onCompleted();
                } catch (Exception e) {
                    observer.onError(e);
                }
            }
        }).subscribeOn(config.subscribeOn()).observeOn(config.observeOn());
    }

    /**
     * Functin used to perform a count query against remote sever for specifed type.
     * @param type Type to be counted.
     * @param <B> Type extending BackendObject
     * @return Count of objects on remote server matching specified type.
     */
    public <B extends BackendObject> Observable<Integer> count(final Class<B> type){
        return getWebService().count(isLoggedIn(),Query.safeTable(type))
                .subscribeOn(config.subscribeOn()).observeOn(config.observeOn());
    }

    /**
     * Used to determine if a user is logged in localy, if not remote operations can not be performed.
     * @return authentication token for logged in user.
     * @throws RuntimeException Execption thrown if if remote operaton is requested but no user is logged in.
     */
    private String isLoggedIn() throws RuntimeException {
        if(authManager != null && authManager.getUser() != null && authManager.getUser().getAuthToken() != null){
            return "CUSTOM " + authManager.getUser().getAuthToken();
        } else {
            throw new RuntimeException("User state error.");
        }
    }

    private static Gson gson = new Gson();

    private <B extends TransientObject> Collection<B> convertRequest(Class<B[]> type, Response response){
        String body = null;
        try {
            body = IOUtils.toString(response.getBody().in());
        } catch (IOException e) {
            e.printStackTrace();
        }
        B[] t = gson.fromJson(body,type);
        return ObjectUtils.v2c(t);
    }

    /**
     * Convience method to convert a specified type to an Array of that specified type.
     * Example: Class<Integer[]> intArrayType = getArrayType(Integer.class);
     * @param type class you wish to have concrete array type of.
     * @return concrete array type of type specified.
     */
    private <T extends TransientObject> Class<T[]> getArrayType(Class<T> type){
        return (Class<T[]>) Array.newInstance(type, 0).getClass();
    }
}
