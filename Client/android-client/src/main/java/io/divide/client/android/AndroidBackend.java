package io.divide.client.android;

import com.google.inject.Inject;
import io.divide.client.Backend;
import io.divide.client.android.push.PushManager;

/**
 * Created by williamwebb on 4/15/14.
 */
public class AndroidBackend extends Backend {

    @Inject PushManager pushManager;
    @Inject AndroidConfig config;

    @Inject
    private AndroidBackend(){
        super();
    }

}
