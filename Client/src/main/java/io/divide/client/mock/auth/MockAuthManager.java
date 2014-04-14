package io.divide.client.mock.auth;

import com.google.inject.Inject;
import io.divide.client.BackendConfig;
import io.divide.client.auth.AuthManager;
import io.divide.client.auth.AuthWebService;
import io.divide.shared.server.DAO;
import io.divide.shared.web.transitory.TransientObject;

import java.security.NoSuchAlgorithmException;

/**
 * Created by williamwebb on 4/6/14.
 */
public class MockAuthManager extends AuthManager {

    @Inject MockAuthWebServer mockAuthWebService;
    @Inject DAO<TransientObject,TransientObject> db;

    @Inject
    public MockAuthManager(BackendConfig config) throws NoSuchAlgorithmException {
        super(config);

//        mockAuthWebService = new MockAuthWebServer(this, db);
    }


    @Override
    public AuthWebService getWebService(){
        return mockAuthWebService;
    }

    @Override
    public void initAdapter(BackendConfig config){}

}
