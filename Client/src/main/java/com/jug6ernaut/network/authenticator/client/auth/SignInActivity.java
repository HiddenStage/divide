package com.jug6ernaut.network.authenticator.client.auth;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.jug6ernaut.android.logging.Logger;
import com.jug6ernaut.network.authenticator.client.DataServices;
import com.jug6ernaut.network.authenticator.client.R;
import com.jug6ernaut.network.authenticator.client.AuthManager;
import com.jug6ernaut.network.shared.web.transitory.Credentials;

import static com.jug6ernaut.network.authenticator.client.AuthManager.ACCOUNT_TYPE;
import static com.jug6ernaut.network.authenticator.client.AuthManager.AUTHTOKEN_TYPE_FULL_ACCESS;

/**
 * The AuthUtils activity.
 *
 * Called by the AuthUtils and in charge of identifing the user.
 *
 * It sends back to the AuthUtils the result.
 */
public class SignInActivity extends AccountAuthenticatorActivity {
    Logger logger = Logger.getLogger(SignInActivity.class);

    public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
    public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";

    public static final String KEY_ERROR_MESSAGE = "ERR_MSG";

    public final static String PARAM_USER_PASS = "USER_PASS";

    private final int REQ_SIGNUP = 1;

    //private AccountManager mAccountManager;
    private String mAuthTokenType;

    //private AuthWebService authWebService;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);
//        mAccountManager = AccountManager.get(getBaseContext());

        String accountName = getIntent().getStringExtra(ARG_ACCOUNT_NAME);
        mAuthTokenType = getIntent().getStringExtra(ARG_AUTH_TYPE);
        if (mAuthTokenType == null)
            mAuthTokenType = AUTHTOKEN_TYPE_FULL_ACCESS;

        if (accountName != null) {
            ((TextView)findViewById(R.id.accountEmail)).setText(accountName);
        }

        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
        findViewById(R.id.signUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Since there can only be one SignInActivity, we call the sign up activity, get his results,
                // and return them in setAccountAuthenticatorResult(). See finishLogin().
                Intent signup = new Intent(getBaseContext(), SignUpActivity.class);
                signup.putExtras(getIntent().getExtras());
                startActivityForResult(signup, REQ_SIGNUP);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // The sign up activity returned that the user has successfully created an account
        if (requestCode == REQ_SIGNUP && resultCode == RESULT_OK) {
            finishLogin(data);
        } else
            super.onActivityResult(requestCode, resultCode, data);
    }

    public void submit() {

        final String userName = ((TextView) findViewById(R.id.accountEmail)).getText().toString();
        final String userPass = ((TextView) findViewById(R.id.accountPassword)).getText().toString();

        final String accountType = getIntent().getStringExtra(ARG_ACCOUNT_TYPE);

        new AsyncTask<String, Void, Intent>() {

            @Override
            protected Intent doInBackground(String... params) {

                logger.debug("> Started authenticating");

                String authtoken;
                Bundle data = new Bundle();
                try {
                    if( (userName==null || userName.trim().length() == 0) ||
                    (accountType == null || accountType.trim().length() == 0) ||
                    (userPass == null || userPass.trim().length() == 0) ){
                        throw new Exception("Bad Params");
                    }

                    Credentials creds = DataServices.get().login(userName,userPass);
                    authtoken = creds.getAuthToken();

                    logger.debug("Token: " + authtoken);

                    data.putString(AccountManager.KEY_ACCOUNT_NAME, userName);
                    data.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType);
                    data.putString(AccountManager.KEY_AUTHTOKEN, authtoken);
                    data.putString(PARAM_USER_PASS, userPass);

                } catch (Exception e) {
                    data.putString(KEY_ERROR_MESSAGE, e.getMessage());
                }

                final Intent res = new Intent();
                res.putExtras(data);
                return res;
            }

            @Override
            protected void onPostExecute(Intent intent) {
                if (intent.hasExtra(KEY_ERROR_MESSAGE)) {
                    Toast.makeText(getBaseContext(), intent.getStringExtra(KEY_ERROR_MESSAGE), Toast.LENGTH_SHORT).show();
                } else {
                    finishLogin(intent);
                }
            }
        }.execute();
    }

    private void finishLogin(Intent intent) {
        logger.debug("> finishLogin");

        String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountPassword = intent.getStringExtra(PARAM_USER_PASS);
//        final Account account = new Account(accountName, intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));

        AuthUtils utils = AuthUtils.get(this, AuthManager.ACCOUNT_TYPE);

        Account account = utils.getAccount(accountName);

        if (account == null) {
            logger.debug("> finishLogin > addAccountExplicitly");

            account = new Account(accountName, ACCOUNT_TYPE);

            String authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
            String authtokenType = mAuthTokenType;

            // Creating the account on the device and setting the auth token we got
            // (Not setting the auth token will cause another call to the server to authenticate the user)
            utils.addAcccount(account, accountPassword, null);
//            mAccountManager.addAccountExplicitly(account, accountPassword, null);
            utils.setAuthToken(account, authtokenType, authtoken);
        } else {
            logger.debug("> finishLogin > setPassword");
            utils.setPassword(account, accountPassword);
        }

        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }

//    private static class SignInDialog extends ActionDialog{
//
//        protected SignInDialog(Activity activity, Callback callback) {
//            super(activity, callback);
//        }
//
//        @Override
//        public View onCreateView(Context context, LayoutInflater inflater) {
//
//
//
//            return null;
//        }
//    }

}
