package io.divide.client.data;

import com.google.gson.Gson;
import com.google.inject.Inject;
import io.divide.client.BackendObject;
import io.divide.client.Config;
import io.divide.client.auth.AuthManager;
import io.divide.client.web.AbstractWebManager;
import io.divide.shared.logging.Logger;
import io.divide.shared.web.transitory.TransientObject;
import io.divide.shared.web.transitory.query.Query;
import org.apache.commons.io.IOUtils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 8/28/13
 * Time: 9:59 PM
 */
public class DataManager extends AbstractWebManager<DataWebService> {

    private static Logger logger = Logger.getLogger(DataManager.class);

    @Inject private AuthManager authManager;

    @Inject
    public DataManager(Config config) {
        super(config);
    }

    @Override
    protected Class<DataWebService> getType() {
        return DataWebService.class;
    }

    public <B extends BackendObject> Observable<String> send(final Collection<B> objects){
        return getWebService().save(isLoggedIn(),objects)
               .subscribeOn(Schedulers.io()).observeOn(config.observerOn());
    }

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
        }).subscribeOn(Schedulers.io()).observeOn(config.observerOn());

    }

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
        }).subscribeOn(Schedulers.io()).observeOn(config.observerOn());
    }

    public <B extends BackendObject> Observable<Integer> count(final Class<B> type){
        return getWebService().count(isLoggedIn(),Query.safeTable(type))
                .subscribeOn(Schedulers.io()).observeOn(config.observerOn());
    }

    private <B extends BackendObject> Observable<Collection<B>> processRequest(final Class<B[]> type,final Response response){
        return Observable.create(new Observable.OnSubscribe<Collection<B>>() {
            @Override
            public void call(Subscriber<? super Collection<B>> observer) {
                try {
                    observer.onNext(convertRequest(type,response));
                    observer.onCompleted();
                } catch (Exception e) {
                    observer.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(config.observerOn());
    }


    private class DataCallback<B extends BackendObject> implements Callback<Response> {
        Callback<Collection<B>> callback;
        Class<B[]> mType;

        public DataCallback(Class<B[]> type, Callback<Collection<B>> callback){
            this.callback = callback;
            mType = type;
        }

        @Override
        public void success(Response response, Response response2) {
            callback.success(convertRequest(mType,response),response);
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            callback.failure(retrofitError);
        }
    }

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
        return Arrays.asList(t);
    }

    private <T extends TransientObject> Class<T[]> getArrayType(Class<T> type){
        return (Class<T[]>) Array.newInstance(type, 0).getClass();
    }
}
