package io.divide.client.android;

import io.divide.client.BackendModule;

/**
 * Created by williamwebb on 7/13/14.
 */
public class AndroidModule extends BackendModule<AndroidBackend,AndroidConfig> {

    @Override
    protected void additionalConfig(AndroidConfig config){
        bind(AndroidConfig.class).toInstance(config);
    }

}
