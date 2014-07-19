package io.divide.client.auth;

import com.google.inject.Inject;
import io.divide.client.Config;

/**
 * Created by williamwebb on 4/6/14.
 */
public class MockAuthManager extends AuthManager {

    MockAuthWebService mockAuthWebService;

    @Inject
    public MockAuthManager(Config config, AccountStorage storage, MockAuthWebService service) {
        super(config, storage);
        mockAuthWebService = service;
        mockAuthWebService.setAuthManger(this);
    }


    @Override
    public AuthWebService getWebService(){
        return mockAuthWebService;
    }

    @Override
    public void initAdapter(Config config){}

}
