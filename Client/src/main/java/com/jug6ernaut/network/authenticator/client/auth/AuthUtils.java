package com.jug6ernaut.network.authenticator.client.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import com.jug6ernaut.android.logging.Logger;
import com.jug6ernaut.network.authenticator.client.Callback;
import com.jug6ernaut.network.authenticator.client.AuthManager;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 7/28/13
 * Time: 9:38 AM
 *
 * Tactics class used to authenticate agains the Tactics Server.
 */
public class AuthUtils {
    private static Logger logger = Logger.getLogger(AuthUtils.class);

    private static Map<String,AuthUtils> utilMap = new ConcurrentHashMap<String, AuthUtils>();

    private AccountManager mAccountManager;
    private String accountType;

    public static AuthUtils get(Context context,String accountType){
        AuthUtils utils;
        if(utilMap.containsKey(accountType)){
            utils = utilMap.get(accountType);
        } else {
            utils = new AuthUtils(context,accountType);
            utilMap.put(accountType,utils);
        }
        return utils;
    }

    private AuthUtils(Context context, String accountType){
        this.mAccountManager = AccountManager.get(context);
        this.accountType = accountType;
    }

    public void addAcccount(Account account,String password, Bundle userData){
        mAccountManager.addAccountExplicitly(account,password,userData);
    }

    public void removeAccount(String accountName){
        Account account = getAccount(accountName);
        if(account!=null)
            mAccountManager.removeAccount(account,null,new Handler());
    }

    public boolean isAuthenticated(String accountName) {
        return (getAccount(accountName) != null);
    }

    public Account getAccount(String accountName){
        Account account=null;
        List<Account> accounts = getAccounts();
        if (accounts.size() == 0) {
            logger.info("No accounts");
            return null;
        } else {
            for (Account a : accounts) {
                if(a.name.equals(accountName)){
                    account = a;
                    break;
                }
            }
        }
        return account;
    }

    public void setAuthToken(android.accounts.Account account, java.lang.String authTokenType, java.lang.String authToken){
        mAccountManager.setAuthToken(account,authTokenType,authToken);
    }

    public void setPassword(Account account, String password){
        mAccountManager.setPassword(account,password);
    }

    public void getAuthToken(Account account,final Callback<String> callback){
        mAccountManager.getAuthToken(account, AuthManager.AUTHTOKEN_TYPE_FULL_ACCESS,null,true,new AccountManagerCallback<Bundle>() {
            Bundle bnd;
            @Override
            public void run(AccountManagerFuture<Bundle> future) {
                try {
                    bnd = future.getResult();
                    logger.debug("Login Results: " + bnd.toString());
                    final String authtoken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
                    callback.callback(authtoken);
                } catch (Exception e) {
                    logger.error("Failed to get authToken",e);
                }
            }
        },new Handler());
    }

    public List<Account> getAccounts(){
        Account availableAccounts[] = mAccountManager.getAccountsByType(accountType);
        return Arrays.asList(availableAccounts);
    }

//    public String getAuthToken(){
//        return authToken;
//    }

//    public void addNewAccount(){
//        addNewAccount(ACCOUNT_TYPE, AUTHTOKEN_TYPE_FULL_ACCESS);
//    }
//
//    /**
//     * Add new account to the account manager
//     * @param accountType
//     * @param authTokenType
//     */
//    private void addNewAccount(String accountType, String authTokenType) {
//        final AccountManagerFuture<Bundle> future = mAccountManager.addAccount(accountType, authTokenType, null, null, context, new AccountManagerCallback<Bundle>() {
//            @Override
//            public void run(AccountManagerFuture<Bundle> future) {
//                try {
//                    Bundle bnd = future.getResult();
//                    showMessage("Account was created");
//                    Log.d("udinic", "AddNewAccount Bundle is " + bnd);
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    showMessage(e.getMessage());
//                }
//            }
//        }, null);
//    }
//
//    /**
//     * Show all the accounts registered on the account manager. Request an auth token upon user select.
//     * @param authTokenType
//     */
//    private void showAccountPicker(final String authTokenType, final boolean invalidate) {
//
//        final Account availableAccounts[] = mAccountManager.getAccountsByType(ACCOUNT_TYPE);
//
//        if (availableAccounts.length == 0) {
//            Toast.makeText(activity, "No accounts", Toast.LENGTH_SHORT).show();
//        } else {
//            String name[] = new String[availableAccounts.length];
//            for (int i = 0; i < availableAccounts.length; i++) {
//                name[i] = availableAccounts[i].name;
//            }
//
//            // Account picker
//            new AlertDialog.Builder(activity).setTitle("Pick Account").setAdapter(new ArrayAdapter<String>(activity.getBaseContext(), android.R.layout.simple_list_item_1, name), new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    if(invalidate)
//                        invalidateAuthToken(availableAccounts[which], authTokenType);
//                    else
//                        getExistingAccountAuthToken(availableAccounts[which], authTokenType);
//                }
//            }).show();
//        }
//    }
//
//    /**
//     * Get the auth token for an existing account on the AccountManager
//     * @param account
//     * @param authTokenType
//     */
//    private void getExistingAccountAuthToken(Account account, String authTokenType) {
//        final AccountManagerFuture<Bundle> future = mAccountManager.getAuthToken(account, authTokenType, null, activity, null, null);
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Bundle bnd = future.getResult();
//
//                    final String authtoken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
//                    showMessage((authtoken != null) ? "SUCCESS!\ntoken: " + authtoken : "FAIL");
//                    Log.d("udinic", "GetToken Bundle is " + bnd);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    showMessage(e.getMessage());
//                }
//            }
//        }).start();
//    }
//
//    /**
//     * Invalidates the auth token for the account
//     * @param account
//     * @param authTokenType
//     */
//    private void invalidateAuthToken(final Account account, String authTokenType) {
//        final AccountManagerFuture<Bundle> future = mAccountManager.getAuthToken(account, authTokenType, null, activity, null,null);
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Bundle bnd = future.getResult();
//
//                    final String authtoken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
//                    mAccountManager.invalidateAuthToken(account.type, authtoken);
//                    showMessage(account.name + " invalidated");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    showMessage(e.getMessage());
//                }
//            }
//        }).start();
//    }

//    public void getTokenForAccountCreateIfNeeded(){
//        getTokenForAccountCreateIfNeeded(ACCOUNT_TYPE, AUTHTOKEN_TYPE_FULL_ACCESS);
//    }
//
//    /**
//     * Get an auth token for the account.
//     * If not exist - add it and then return its auth token.
//     * If one exist - return its auth token.
//     * If more than one exists - show a picker and return the select account's auth token.
//     * @param accountType
//     * @param authTokenType
//     */
//    private void getTokenForAccountCreateIfNeeded(String accountType, String authTokenType) {
//        final AccountManagerFuture<Bundle> future = mAccountManager.getAuthTokenByFeatures(accountType, authTokenType, null, context, null, null,
//                new AccountManagerCallback<Bundle>() {
//                    @Override
//                    public void run(AccountManagerFuture<Bundle> future) {
//                        Bundle bnd = null;
//                        try {
//                            bnd = future.getResult();
//                            final String authtoken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
//                            showMessage(((authtoken != null) ? "SUCCESS!\ntoken: " + authtoken : "FAIL"));
//                            Log.d("udinic", "GetTokenForAccount Bundle is " + bnd);
//
//                            setAuthToken(authtoken);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            showMessage(e.getMessage());
//                        }
//                    }
//                }
//                , null);
//    }

//    private void showMessage(final String msg) {
//        if (msg == null || msg.trim().equals(""))
//            return;
//
//        activity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(activity.getBaseContext(), msg, Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

//    public void setAuthToken(String token){
//        authToken = Base64.encode(token.getBytes());
//    }

}
