package io.divide.client.android;

import io.divide.client.BackendModule;
import io.divide.client.Config;

/**
 * Created by williamwebb on 7/13/14.
 */
public class AndroidModule extends BackendModule<AndroidBackend> {

    public AndroidModule(){

    }

    @Override
    public void additionalConfig(Config<AndroidBackend> config){
        bind(AndroidConfig.class).toInstance((AndroidConfig) config);
    }
}
