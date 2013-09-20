package com.jug6ernaut.network.authenticator.client.auth;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;
import java.util.TreeSet;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 8/28/13
 * Time: 10:23 PM
 */
public class AccountStorage {

    public static final String FILE_NAME = "account_storage";
    public static final String SOME_KEY = "a;lakjdf;alskjf;aslkdfj;askdfj;asdfkas;dfka;sdf";

//    private SecurePreferences storage;
    private SharedPreferences storage;
    private static AccountStorage accountStorage;

    public static synchronized AccountStorage get(Context context){
        if(accountStorage==null){
            accountStorage = new AccountStorage(context);
        }
        return accountStorage;
    }
    private AccountStorage(Context context){
//        storage = new SecurePreferences(context, FILE_NAME, SOME_KEY, true);
        storage = context.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE);
    }

    public synchronized void clear(String accountType){
        storage.edit().remove(accountType);
    }

    public synchronized Set<String> getAccounts(String type){
        return storage.getStringSet(type,null);
    }

    public synchronized void addAccount(String type, String name){
        Set<String> accounts = getAccounts(type);
        if(accounts == null) accounts = new TreeSet<String>();
        accounts.add(name);
        storage.edit().putStringSet(type,accounts).commit();
    }

    public synchronized void removeAccount(String type, String name){
        Set<String> accounts = getAccounts(type);
        if(accounts == null) accounts = new TreeSet<String>();
        accounts.remove(name);
        storage.edit().putStringSet(type,accounts).commit();
    }

}
