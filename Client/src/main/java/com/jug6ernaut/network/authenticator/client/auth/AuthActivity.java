package com.jug6ernaut.network.authenticator.client.auth;

import android.accounts.AccountAuthenticatorActivity;
import android.os.Bundle;
import com.jug6ernaut.android.logging.Logger;
import com.jug6ernaut.network.authenticator.client.BackendServices;

/**
 * The AuthUtils activity.
 *
 * Called by the AuthUtils and in charge of identifing the user.
 *
 * It sends back to the AuthUtils the result.
 */
public class AuthActivity extends AccountAuthenticatorActivity {
    Logger logger = Logger.getLogger(AuthActivity.class);

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new CredentialView(this,"client-example"));
        BackendServices.addLoginListener(new LoginListener() {

            @Override
            public void onNext(LoginEvent loginEvent) {
                switch(loginEvent.state){
                    case LOGGED_IN:
                        AuthActivity.this.finish();
                        break;
                }
            }
        });
    }

}
