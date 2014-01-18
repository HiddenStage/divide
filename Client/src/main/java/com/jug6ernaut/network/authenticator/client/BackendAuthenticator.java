package com.jug6ernaut.network.authenticator.client;

import android.accounts.*;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;
import com.jug6ernaut.android.logging.Logger;
import com.jug6ernaut.network.authenticator.client.auth.*;
import com.jug6ernaut.network.authenticator.client.auth.credentials.LoginCredentials;

import java.util.Arrays;
import java.util.List;

import static android.accounts.AccountManager.KEY_BOOLEAN_RESULT;

/**
 * Created with IntelliJ IDEA.
 * User: Udini
 * Date: 19/03/13
 * Time: 18:58
 */
public class BackendAuthenticator extends AbstractAccountAuthenticator {

    public static final int ERROR_CODE_ONE_ACCOUNT_ALLOWED = -2;

    private static final Logger logger = Logger.getLogger(BackendAuthenticator.class);
    private final Context mContext;
    private final Handler handler = new Handler();
    private AuthUtils authUtils;
    private AccountInformation accountInformation;
    List<String> tokenTypes;

    public BackendAuthenticator(Context context) {
        super(context);
        // I hate you! Google - set mContext as protected!
        this.mContext = context;
        this.accountInformation = Backend.get().accountInformation;
        authUtils = AuthUtils.get(context, accountInformation.getAccountType());
        tokenTypes = Arrays.asList(accountInformation.getFullAccessTokenType(),accountInformation.getReadAccessTokenType());
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        logger.debug("> addAccount: " + accountType + " : " + authTokenType + " : " + Arrays.asList(requiredFeatures) + " : " + options);

        if (authUtils.getAccounts().size() > 0) {
            final Bundle result = new Bundle();
            result.putInt(AccountManager.KEY_ERROR_CODE, ERROR_CODE_ONE_ACCOUNT_ALLOWED);
            result.putString(AccountManager.KEY_ERROR_MESSAGE, "Only one account allowed");

            handler.post(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(mContext, "Only one account allowed", Toast.LENGTH_SHORT).show();
                }
            });
            return result;
        }

        final Intent intent = new Intent(mContext, AuthActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);

        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        logger.debug("> getAuthToken("+account+"): " + authTokenType);

        // If the caller requested an authToken type we don't support, then
        // return an error
        if (!authTokenType.equals(accountInformation.getFullAccessTokenType()) && !authTokenType.equals(accountInformation.getReadAccessTokenType())) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ERROR_MESSAGE, "invalid authTokenType[" + tokenTypes +"] : " + authTokenType);

            return result;
        }

        // Extract the username and password from the Account Manager, and ask
        // the server for an appropriate AuthToken.
        final AccountManager am = AccountManager.get(mContext);

        String authToken = am.peekAuthToken(account, authTokenType);

        logger.debug("> peekAuthToken returned - " + authToken);

        // Lets give another try to authenticate the user
        if (TextUtils.isEmpty(authToken)) {
            final String password = am.getPassword(account);
            if (password != null) {
                try {
                    logger.debug("> re-authenticating with the existing password");

                    LoginCredentials loginCredentials = new LoginCredentials(account.name,password);
                    loginCredentials.setEncrypted(true);

                    AuthManager manager = Backend.get().getAuthManager();
                    RecoveryResponse recoveryResponse = manager.recoverFromOneTimeToken(password);

                    if(recoveryResponse.getStatus().isSuccess()){
                        BackendUser user = recoveryResponse.get();
                        logger.debug(user);
                        authToken = user.getAuthToken();
                        logger.debug("AuthToken: " + authToken);
                    } else {
                        logger.error("Failed to re-authenticate: " + recoveryResponse.getError());
                    }
                } catch (Exception e) {
                    logger.error("",e);
                }
            }
        }

        // If we get an authToken - we return it
        if (!TextUtils.isEmpty(authToken)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            return result;
        }

        // If we get here, then we couldn't access the user's password - so we
        // need to re-prompt them for their credentials. We do that by creating
        // an intent to display our AuthActivity.
        final Intent intent = new Intent(mContext, AuthActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }


    @Override
    public String getAuthTokenLabel(String authTokenType) {
        if (accountInformation.getFullAccessTokenType().equals(authTokenType))
            return accountInformation.getFullAccessTokenLabel();
        else if (accountInformation.getReadAccessTokenType().equals(authTokenType))
            return accountInformation.getReadAccessTokenLabel();
        else
            return authTokenType + " (Label)";
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
        final Bundle result = new Bundle();
        result.putBoolean(KEY_BOOLEAN_RESULT, false);
        return result;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        return null;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        return null;
    }
}
