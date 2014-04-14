package io.divide.client.mock.push;

import com.google.gson.Gson;
import com.google.inject.Inject;
import io.divide.client.auth.AuthManager;
import io.divide.client.push.PushWebService;
import io.divide.shared.server.DAO;
import io.divide.shared.server.KeyManager;
import io.divide.shared.web.transitory.Credentials;
import io.divide.shared.web.transitory.EncryptedEntity;
import io.divide.shared.web.transitory.TransientObject;
import retrofit.Callback;
import retrofit.http.Body;

import static io.divide.shared.server.DAO.DAOException;
import static io.divide.shared.web.transitory.EncryptedEntity.Reader;

/**
 * Created by williamwebb on 4/6/14.
 */
public class MockPushWebService implements PushWebService {

    @Inject private DAO<TransientObject,TransientObject> dao;
    @Inject private AuthManager authManager;
    @Inject private KeyManager keyManager;

    @Override
    public Boolean register(@Body EncryptedEntity ent) {
        try{
            Reader entity = convert(Reader.class, ent);

            Credentials credentials = authManager.getUser();
            entity.setKey(keyManager.getPrivateKey());

            credentials.setPushMessagingKey(entity.get("token"));
            dao.save(credentials);
            return true;
        } catch (DAOException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void register(@Body EncryptedEntity ent, Callback<Boolean> callback) {
        try{
            Reader entity = convert(Reader.class, ent);

            Credentials credentials = authManager.getUser();
            entity.setKey(keyManager.getPrivateKey());

            credentials.setPushMessagingKey(entity.get("token"));
            dao.save(credentials);
            callback.success(true,null);
        } catch (DAOException e) {
            callback.success(false,null);
        } catch (Exception e) {
            callback.success(false,null);
        }
    }

    @Override
    public Boolean unregister() {
        try{
            Credentials credentials = authManager.getUser();
            credentials.setPushMessagingKey("");
            dao.save(credentials);
            return true;
        } catch (DAOException e) {
            return false;
        }
    }

    @Override
    public void unregister(Callback<Boolean> callback) {
        try{
            Credentials credentials = authManager.getUser();
            credentials.setPushMessagingKey("");
            dao.save(credentials);
            callback.success(true,null);
        } catch (DAOException e) {
            callback.failure(null);
        }
    }

    private static Gson converter = new Gson();
    private static <X, T extends X> T convert(Class<T> type, X from){
        return converter.fromJson(converter.toJson(from),type);
    }
}
