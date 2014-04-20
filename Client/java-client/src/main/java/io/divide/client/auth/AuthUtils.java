//package io.divide.client.auth;
//
//import android.accounts.Account;
//import android.accounts.AccountManager;
//import android.accounts.AccountManagerCallback;
//import android.accounts.AccountManagerFuture;
//import android.content.Context;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import com.jug6ernaut.android.logging.Logger;
//import io.divide.client.data.Callback;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * Created with IntelliJ IDEA.
// * User: williamwebb
// * Date: 7/28/13
// * Time: 9:38 AM
// *
// * Tactics class used to authenticate agains the Tactics Server.
// */
//public class AuthUtils {
//    private static Logger logger = Logger.getLogger(AuthUtils.class);
//
//    private static Map<String,AuthUtils> utilMap = new ConcurrentHashMap<String, AuthUtils>();
//
//    private AccountManager mAccountManager;
//    private String accountType;
//    private final Handler handler = new Handler();
//
//    public static AuthUtils get(Context context,String accountType){
//        AuthUtils utils;
//        if(utilMap.containsKey(accountType)){
//            utils = utilMap.get(accountType);
//        } else {
//            utils = new AuthUtils(context,accountType);
//            utilMap.put(accountType,utils);
//        }
//        return utils;
//    }
//
//    private AuthUtils(Context context, String accountType){
//        this.mAccountManager = AccountManager.get(context);
//        this.accountType = accountType;
//    }
//
//    public void addAcccount(Account account,String password, Bundle userData){
//        mAccountManager.addAccountExplicitly(account,password,userData);
//    }
//
//    public void removeAccount(String accountName){
//        Account account = getAccount(accountName);
//        if(account!=null)
//            mAccountManager.removeAccount(account,null,handler);
//    }
//
//    public boolean isAuthenticated(String accountName) {
//        return (getAccount(accountName) != null);
//    }
//
//    public Account getAccount(String accountName){
//        Account account=null;
//        List<Account> accounts = getAccounts();
//        if (accounts.size() == 0) {
//            logger.info("No accounts");
//            return null;
//        } else {
//            for (Account a : accounts) {
//                if(a.name.equals(accountName)){
//                    account = a;
//                    break;
//                }
//            }
//        }
//        return account;
//    }
//
//    public void setAuthToken(android.accounts.Account account, java.lang.String authTokenType, java.lang.String authToken){
//        mAccountManager.setAuthToken(account,authTokenType,authToken);
//    }
//
//    public void invalidateToken(java.lang.String authTokenType, java.lang.String authToken){
//        mAccountManager.invalidateAuthToken(authTokenType,authToken);
//    }
//
//    public void setPassword(Account account, String password){
//        mAccountManager.setPassword(account,password);
//    }
//
//    public void getAuthToken(Account account, String authTokenType, final Callback<String> callback){
//
//        mAccountManager.getAuthToken(account, authTokenType,null,true,new AccountManagerCallback<Bundle>() {
//            Bundle bnd;
//            @Override
//            public void run(AccountManagerFuture<Bundle> future) {
//                try {
//                    bnd = future.getResult();
//                    final String authtoken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
//                    callback.onResult(authtoken);
//                } catch (Exception e) {
//                    callback.onError(e);
//                    logger.error("Failed to get authToken",e);
//                }
//            }
//        },new Handler(Looper.getMainLooper()));
//    }
//
//    public String getPassword(Account account){
//        return mAccountManager.getPassword(account);
//    }
//
//    public String blockingGetAuthToken(Account account, String authTokenType) throws Exception{
//        try {
//            return mAccountManager.blockingGetAuthToken(account,authTokenType,true);
//        } catch (Exception e) {
//            throw new Exception("Failed to get auth_token from manager.",e);
//        }
//    }
//
//    public List<Account> getAccounts(){
//        Account availableAccounts[] = mAccountManager.getAccountsByType(accountType);
//        return Arrays.asList(availableAccounts);
//    }
//
//}
