package com.jug6ernaut.network.authenticator.client.auth;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.jug6ernaut.android.logging.Logger;
import com.jug6ernaut.network.authenticator.client.BackendUser;
import com.jug6ernaut.network.authenticator.client.DataServices;
import com.jug6ernaut.network.authenticator.client.R;
import com.jug6ernaut.network.shared.web.transitory.Credentials;

/**
 * In charge of the Sign up process. Since it's not an SignInActivity decendent,
 * it returns the result back to the calling activity, which is an SignInActivity,
 * and it return the result back to the AuthUtils
 *
 * User: udinic
 */
public class SignUpActivity extends Activity {
    Logger logger = Logger.getLogger(SignUpActivity.class);

    private String mAccountType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAccountType = getIntent().getStringExtra(SignInActivity.ARG_ACCOUNT_TYPE);

        setContentView(R.layout.act_register);

        findViewById(R.id.alreadyMember).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });

        if (AuthUtils.get(this,mAccountType).getAccounts().size() > 0) {
            final Bundle result = new Bundle();
            result.putInt(AccountManager.KEY_ERROR_CODE, AccountAuthenticator.ERROR_CODE_ONE_ACCOUNT_ALLOWED);
            result.putString(AccountManager.KEY_ERROR_MESSAGE, "Only one account allowed");

            new Handler().post(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(SignUpActivity.this, "Only one account allowed", Toast.LENGTH_SHORT).show();
                }
            });
            finish();
        }
    }

    private void createAccount() {

        // Validation!

        new AsyncTask<String, Void, Intent>() {

            String name = ((TextView) findViewById(R.id.name)).getText().toString().trim();
            String emailAddress = ((TextView) findViewById(R.id.accountEmail)).getText().toString().trim();
            String accountPassword = ((TextView) findViewById(R.id.accountPassword)).getText().toString().trim();

            @Override
            protected Intent doInBackground(String... params) {

                logger.debug("Started authenticating");

                String authtoken = null;
                Bundle data = new Bundle();
                try {
                    if( (name==null || name.trim().length() == 0) ||
                    (emailAddress == null || emailAddress.trim().length() == 0) ||
                    (accountPassword == null || accountPassword.trim().length() == 0) ||
                    (mAccountType == null || mAccountType.trim().length() == 0) ){
                        throw new Exception("Bad Params");
                    }

                    BackendUser user = BackendUser.create(emailAddress,name,accountPassword);

                    Credentials credentials = DataServices.get().signUp(user);
                    authtoken = credentials.getAuthToken();
//                    authtoken = sServerAuthenticate.userSignUp(name, accountName, accountPassword, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS);

                    logger.debug(AccountManager.KEY_ACCOUNT_NAME + ": " + name);
                    logger.debug(AccountManager.KEY_ACCOUNT_TYPE + ": " + mAccountType);
                    logger.debug(AccountManager.KEY_AUTHTOKEN + ": " + authtoken);
                    logger.debug(SignInActivity.PARAM_USER_PASS + ": " + accountPassword);

                    data.putString(AccountManager.KEY_ACCOUNT_NAME, name);
                    data.putString(AccountManager.KEY_ACCOUNT_TYPE, mAccountType);
                    data.putString(AccountManager.KEY_AUTHTOKEN, authtoken);
                    data.putString(SignInActivity.PARAM_USER_PASS, accountPassword);
                } catch (Exception e) {
                    data.putString(SignInActivity.KEY_ERROR_MESSAGE, e.getMessage());
                    logger.fatal("failed to start signup",e);
                }

                final Intent res = new Intent();
                res.putExtras(data);
                return res;
            }

            @Override
            protected void onPostExecute(Intent intent) {
                if (intent.hasExtra(SignInActivity.KEY_ERROR_MESSAGE)) {
                    Toast.makeText(getBaseContext(), intent.getStringExtra(SignInActivity.KEY_ERROR_MESSAGE), Toast.LENGTH_SHORT).show();
                } else {
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        }.execute();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}
