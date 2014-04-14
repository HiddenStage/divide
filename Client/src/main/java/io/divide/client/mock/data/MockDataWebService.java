package io.divide.client.mock.data;

import com.google.inject.Inject;
import io.divide.client.BackendObject;
import io.divide.client.data.DataWebService;
import io.divide.client.mock.GsonResponse;
import io.divide.shared.server.DAO;
import io.divide.shared.web.transitory.TransientObject;
import io.divide.shared.web.transitory.query.Query;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.EncodedPath;
import rx.Observable;
import rx.Subscriber;

import java.util.Collection;

/**
 * Created by williamwebb on 4/6/14.
 */
public class MockDataWebService implements DataWebService {

    @Inject private DAO<TransientObject,TransientObject> dao;

    @Override
    public Response get(@EncodedPath("objectType") String objectType, @Body Collection<String> keys) {
        try {
            Collection<TransientObject> got = dao.get(objectType, keys.toArray(new String[keys.size()]));
            return new GsonResponse("",200,"",null, got).build();
        } catch (DAO.DAOException e) {
            return new GsonResponse("",e.getStatusCode(),e.getMessage(), null, null).build();
        }
    }

    @Override
    public Response query(@Body Query query) {
        try {
            Collection<TransientObject> got = dao.query(query);
            return new GsonResponse("",200,"",null, got).build();
        } catch (DAO.DAOException e) {
            return new GsonResponse("",e.getStatusCode(),e.getMessage(), null, null).build();
        }    }

    @Override
    public <B extends BackendObject> Observable<String> save(final @Body Collection<B> objects) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    dao.save((B[]) objects.toArray());
                    subscriber.onNext("");
                } catch (DAO.DAOException e) {
                    subscriber.onError(e);
                };
            }
        });
    }

    @Override
    public Observable<Integer> count(@EncodedPath("objectType") String objectType) {
        return Observable.from(dao.count(objectType));
    }
}
