package io.divide.client.auth;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import io.divide.client.BackendAuthenticator;

/**
 * Created with IntelliJ IDEA.
 * User: Udini
 * Date: 19/03/13
 * Time: 19:10
 */
public class AuthService extends Service {
    @Override
    public IBinder onBind(Intent intent) {

        BackendAuthenticator authenticator = new BackendAuthenticator(this);
        return authenticator.getIBinder();
    }
}
