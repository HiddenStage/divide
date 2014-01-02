package com.jug6ernaut.network.authenticator.client.auth;

import android.content.Context;

/**
 * Created by williamwebb on 12/30/13.
 */
public class AccountInformation {

    private String accountName;
    private String accountType;

    public AccountInformation(Context context){
        accountName = context.getApplicationInfo().name;
        accountType = context.getPackageName();
    }

    public String getAccountName(){
        return accountName;
    };

    public String getAccountType(){
        return accountType;
    };

    public String getFullAccessTokenType(){
        return accountType + ".full";
    };

    public String getFullAccessTokenLabel(){
        return accountName + "_Full";
    };

    public String getReadAccessTokenType(){
        return accountType + ".readonly";
    };

    public String getReadAccessTokenLabel(){
        return accountName + "_ReadOnly";
    };
}
