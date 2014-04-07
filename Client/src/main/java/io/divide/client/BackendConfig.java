package io.divide.client;

import android.app.Application;
import com.squareup.okhttp.OkHttpClient;
import io.divide.client.auth.AccountInformation;

/**
 * Created by williamwebb on 4/4/14.
 */
public class BackendConfig {

    public Application app;
    public final String serverUrl;
    public long id;
    public OkHttpClient client;
    public AccountInformation accountInformation;

    public BackendConfig(Application application, String url, long id){
        this.app = application;
        this.serverUrl = url;
        this.id = id;
        this.client = new OkHttpClient();
        this.accountInformation = new AccountInformation(application);
    };

}
