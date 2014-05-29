package io.divide.client.android;

import com.google.inject.Inject;
import io.divide.client.Backend;
import io.divide.client.BackendConfig;
import io.divide.client.android.push.PushManager;

/**
 * Created by williamwebb on 4/15/14.
 */
public class AndroidBackend extends Backend {

    @Inject
    PushManager pushManager;

    private AndroidBackend(BackendConfig config){
        super();
//        config.setBackendClass(AndroidBackend.class);
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
