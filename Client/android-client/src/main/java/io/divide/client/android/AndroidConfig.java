package io.divide.client.android;

import android.app.Application;
import io.divide.client.BackendConfig;
import io.divide.client.android.security.PRNGFixes;
import rx.android.schedulers.AndroidSchedulers;

import java.io.File;

/**
 * Created by williamwebb on 4/19/14.
 */
public class AndroidConfig extends BackendConfig {

    static { PRNGFixes.apply(); }

    public Application app;

    public AndroidConfig(Application application, String url){
        this(application,url,System.currentTimeMillis());
    }

    public AndroidConfig(Application application, String url, long id) {
        super(application.getFilesDir().getPath() + File.separator, url, id);
        this.app = application;
        this.observerOn(AndroidSchedulers.mainThread());
    }
}
