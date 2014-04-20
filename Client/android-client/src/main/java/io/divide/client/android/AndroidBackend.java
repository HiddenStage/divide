package io.divide.client.android;

import io.divide.client.Backend;
import io.divide.client.BackendConfig;

/**
 * Created by williamwebb on 4/15/14.
 */
public class AndroidBackend extends Backend {

    private AndroidBackend(BackendConfig config){
        super();
    }

//    @Override
//    protected Injector getInjector(BackendModule module){
//        RoboGuice.setBaseApplicationInjector(
//                application,
//                RoboGuice.DEFAULT_STAGE,
//                RoboGuice.newDefaultRoboModule(context),
//                new BackendModule(new BackendConfig(context, serverUrl, System.currentTimeMillis())));
//    }
}
