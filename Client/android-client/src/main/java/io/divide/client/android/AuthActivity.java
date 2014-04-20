package io.divide.client.android;

import android.accounts.AccountAuthenticatorActivity;
import android.os.Bundle;
import io.divide.client.BackendServices;
import io.divide.client.BackendUser;
import io.divide.client.auth.LoginListener;
import io.divide.shared.logging.Logger;

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
        setContentView(new CredentialView(this, "client-example"));
        BackendServices.addLoginListener(new LoginListener() {
            @Override
            public void onNext(BackendUser backendUser) {
                if(backendUser != null){
                    AuthActivity.this.finish();
                }
            }
        });
    }

}
