package io.divide.client.data;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.jug6ernaut.android.logging.Logger;
import io.divide.client.web.AbstractWebManager;
import io.divide.client.BackendConfig;
import io.divide.client.BackendObject;
import io.divide.shared.web.transitory.TransientObject;
import io.divide.shared.web.transitory.query.Query;
import org.apache.commons.io.IOUtils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

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

    @Inject
    public DataManager(BackendConfig config) {
        super(config);
    }

    @Override
    protected Class<DataWebService> getType() {
        return DataWebService.class;
    }

    public <B extends BackendObject> Observable<String> send(final Collection<B> objects){
        return getWebService().save(objects)
               .subscribeOn(Schedulers.io())
               .observeOn(AndroidSchedulers.mainThread());

    }

    public <B extends BackendObject> Observable<Collection<B>> get(final Class<B> type, final Collection<String> objects){
        return Observable.create(new Observable.OnSubscribeFunc<Collection<B>>() {
            @Override
            public Subscription onSubscribe(Observer<? super Collection<B>> observer) {
                try {
                    observer.onNext(convertRequest(getArrayType(type), getWebService().get(Query.safeTable(type), objects)));
                    observer.onCompleted();
                } catch (Exception e) {
                    observer.onError(e);
                }

                return Subscriptions.empty();
            }
        }).subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread());

    }

    public <B extends BackendObject> Observable<Collection<B>> query(final Class<B> type,final Query query){
        return Observable.create(new Observable.OnSubscribeFunc<Collection<B>>() {
            @Override
            public Subscription onSubscribe(Observer<? super Collection<B>> observer) {
                try {
                    observer.onNext(convertRequest(getArrayType(type),getWebService().query(query)));
                    observer.onCompleted();
                } catch (Exception e) {
                    observer.onError(e);
                }

                return Subscriptions.empty();
            }
        }).subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread());
    }

    public <B extends BackendObject> Observable<Integer> count(final Class<B> type){
        return getWebService().count(Query.safeTable(type));
    }

    private <B extends BackendObject> Observable<Collection<B>> processRequest(final Class<B[]> type,final Response response){
        return Observable.create(new Observable.OnSubscribeFunc<Collection<B>>() {
            @Override
            public Subscription onSubscribe(Observer<? super Collection<B>> observer) {
                try {
                    observer.onNext(convertRequest(type,response));
                    observer.onCompleted();
                } catch (Exception e) {
                    observer.onError(e);
                }

                return Subscriptions.empty();
            }
        }).subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread());

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
