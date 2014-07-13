package io.divide.client.android;

import android.app.Application;
import io.divide.client.Config;
import io.divide.client.android.security.PRNGFixes;
import rx.android.schedulers.AndroidSchedulers;

import java.io.File;

/**
 * Created by williamwebb on 4/19/14.
 */
public class AndroidConfig extends Config<AndroidBackend>{

    static { PRNGFixes.apply(); }

    public Application app;

    public AndroidConfig(Application application, String url) {
        super(application.getFilesDir().getPath() + File.separator, url, AndroidModule.class);
        this.app = application;
        this.observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Class<AndroidBackend> getType() {
        return AndroidBackend.class;
    }
}
