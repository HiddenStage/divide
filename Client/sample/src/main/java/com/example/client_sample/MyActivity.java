package com.example.client_sample;

import android.accounts.Account;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Views;
import com.google.android.gcm.GCMRegistrar;
import com.jug6ernaut.android.logging.Logger;
import com.jug6ernaut.network.authenticator.client.BackendObject;
import com.jug6ernaut.network.authenticator.client.BackendUser;
import com.jug6ernaut.network.authenticator.client.DataServices;
import com.jug6ernaut.network.authenticator.client.AuthManager;
import com.jug6ernaut.network.authenticator.client.auth.AuthUtils;
import com.jug6ernaut.network.authenticator.client.auth.SignInActivity;
import com.jug6ernaut.network.shared.web.transitory.Credentials;
import com.jug6ernaut.network.shared.web.transitory.query.Query;
import com.jug6ernaut.network.shared.web.transitory.query.QueryBuilder;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */

    private MyApplication app;
//    private AccountStorage as;
    private AuthUtils au;

    @InjectView(R.id.loginButton)
    Button login;
    @InjectView(R.id.clearSavedUserButton)
    Button clear;
    @InjectView(R.id.getUsersButton)
    Button getUsers;
    @InjectView(R.id.spkButton)
    Button setPushToken;

    @InjectView(R.id.cachedUserTV)
    TextView savedUserTV;
    @InjectView(R.id.loggedInUserTV)
    TextView loggedInUserTV;
    @InjectView(R.id.urlTV)
    TextView urlTV;

    @InjectView(R.id.usersLV)
    ListView usersLV;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Views.inject(this);

//        as = AccountStorage.get(this);
        au = AuthUtils.get(this,AuthManager.ACCOUNT_TYPE);
        app = (MyApplication) this.getApplication();

        urlTV.setText("URL: " + app.getUrl());

        DataServices.get().addLoginListener(new AuthManager.LoginListener() {
            @Override
            public void onLogin(final BackendUser user) {
                MyActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Logger.getLogger(MyActivity.class).info("loginListener: setUser: " + user);
                        setUser(user);
                    }
                });
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        setUser(DataServices.get().getUser());
    }

    @OnClick(R.id.loginButton)
    public void login(){
        Intent intent = new Intent(MyActivity.this, SignInActivity.class);
        intent.putExtra(SignInActivity.ARG_ACCOUNT_TYPE,AuthManager.ACCOUNT_TYPE);
        MyActivity.this.startActivity(intent);
    }

    @OnClick(R.id.clearSavedUserButton)
    public void logout(){
        au.removeAccount(getSavedUser(au));
        DataServices.get().logout();
        GCMRegistrar.unregister(this);
        savedUserTV.setText("Saved User: ");
        loggedInUserTV.setText("User: ");
    }

    @OnClick(R.id.getUsersButton)
    public void getUseres(){
        Query q = new QueryBuilder().select().from(Credentials.TYPE).limit(10).build();
        DataServices.get().query(q, new Callback<Collection<BackendObject>>() {
            @Override
            public void success(Collection<BackendObject> objects, Response response) {
                usersLV.setAdapter(new UserAdapter(MyActivity.this,new ArrayList<BackendObject>(objects)));
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace();
            }
        });

    }
    @OnClick(R.id.spkButton)
    public void setPushToken(){
        new Thread(){
            @Override
            public void run(){
                DataServices.get().setPushToken("someToken");
            }
        }.start();
    }

    private void setUser(BackendUser user){
        savedUserTV.setText("Cached User: " + getSavedUser(au));
        loggedInUserTV.setText("User: " + user);
    }

    private String getSavedUser(AuthUtils au){
        List<Account> accountList = au.getAccounts();
        if(accountList.size() == 1){
            return accountList.get(0).name;
        } else return "";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        SubMenu subMenu1 = menu.addSubMenu(MENU_LEVEL_1, 0, 0, "Menu");

        subMenu1.add(MENU_LEVEL_1, 0, MENU_SWITCH_URL, "Switch URL");
        subMenu1.add(MENU_LEVEL_1, 0, MENU_SET_URL, "Set URL");

        MenuItem subMenu1Item = subMenu1.getItem();
        subMenu1Item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return super.onCreateOptionsMenu(menu);
    }

    public static final int MENU_GROUP_ID = 1;

    private static final int MENU_LEVEL_0 = 0;
    private static final int MENU_LEVEL_1 = 1;
    private static final int MENU_SWITCH_URL = 1;
    private static final int MENU_SET_URL = 2;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getGroupId() != MENU_GROUP_ID) return super.onOptionsItemSelected(item);

        switch (item.getGroupId()) {
            case MENU_LEVEL_0: {

            }
            break;

            case MENU_LEVEL_1:

                switch (item.getOrder()) {

                    case MENU_SWITCH_URL: {
                        logout();
                        String url = app.switchUrl();
                        app.initBackend(url);
                        urlTV.setText("URL: " + url);
                    }
                    break;
                    case MENU_SET_URL: {
                        AlertDialog ad = new AlertDialog.Builder(this).create();
                        final EditText et = new EditText(this);
                        et.setText(app.getUrl());
                        ad.setView(et);
                        ad.setButton(DialogInterface.BUTTON_POSITIVE,"Set",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                logout();
                                String url = et.getText().toString();
                                app.initBackend(url);
                                urlTV.setText("URL: " + url);
                            }
                        });
                        ad.show();
                    }
                    break;
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
