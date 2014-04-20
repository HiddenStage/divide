package io.divide.client.android;

import android.app.Activity;
import android.content.Context;
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
public class AuthActivity extends Activity {
    Logger logger = Logger.getLogger(AuthActivity.class);

    public static final String TITLE_EXTRA = "title_extra_key";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String title = this.getIntent().getStringExtra(TITLE_EXTRA);
        if(title == null) title = getApplicationName(this);
        setContentView(new CredentialView(this,title));
        BackendServices.addLoginListener(new LoginListener() {
            @Override
            public void onNext(BackendUser backendUser) {
                if(backendUser != null){
                    AuthActivity.this.finish();
                }
            }
        });
    }

    private static String getApplicationName(Context context) {
        int stringId = context.getApplicationInfo().labelRes;
        return context.getString(stringId);
    }

}
