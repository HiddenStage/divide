package com.example.client_sample;

import android.accounts.Account;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.jug6ernaut.android.logging.Logger;
import com.jug6ernaut.android.utilites.time.Stopwatch;
import com.jug6ernaut.network.authenticator.client.BackendObject;
import com.jug6ernaut.network.authenticator.client.BackendServices;
import com.jug6ernaut.network.authenticator.client.BackendUser;
import com.jug6ernaut.network.authenticator.client.auth.LoginState;
import com.jug6ernaut.network.authenticator.client.data.ObjectManager;
import com.jug6ernaut.network.authenticator.client.auth.AuthManager;
import com.jug6ernaut.network.authenticator.client.auth.AuthUtils;
import com.jug6ernaut.network.authenticator.client.auth.AuthActivity;
import com.jug6ernaut.network.authenticator.client.cache.LocalStorageNoSQL;
import com.jug6ernaut.network.shared.web.transitory.TransientObject;
import com.jug6ernaut.network.shared.web.transitory.query.OPERAND;
import com.jug6ernaut.network.shared.web.transitory.query.Query;
import com.jug6ernaut.network.shared.web.transitory.query.QueryBuilder;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class MyActivity extends Activity {

    private Logger logger = Logger.getLogger(MyActivity.class);
    private MyApplication app;
//    private AuthUtils au;
    private List<BackendObject> objectList = new ArrayList<BackendObject>();
    private BackendObjectAdaper adapter;
    private BackendUser user;

    @InjectView(R.id.loginButton)          Button login;
    @InjectView(R.id.clearSavedUserButton) Button clear;
    @InjectView(R.id.getObjects)           Button getObjects;
    @InjectView(R.id.addObject)            Button addObject;
    @InjectView(R.id.cachedUserTV)         TextView savedUserTV;
    @InjectView(R.id.loggedInUserTV)       TextView loggedInUserTV;
    @InjectView(R.id.urlTV)                TextView urlTV;
    @InjectView(R.id.usersLV)              ListView usersLV;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ButterKnife.inject(this);

//        au = AuthUtils.get(this, AuthManager.ACCOUNT_TYPE); TODO replace this
        app = (MyApplication) this.getApplication();

        urlTV.setText("URL: " + app.getUrl());

        BackendServices.addLoginListener(new AuthManager.LoginListener() {
            @Override
            public void onLogin(final BackendUser user, LoginState state) {
                if(LoginState.LOGGED_IN.equals(state))
                MyActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Logger.getLogger(MyActivity.class).info("loginListener: setUser: " + user);
                        setUser(user);
                    }
                });
            }
        });

        adapter = new BackendObjectAdaper(this,objectList);
        usersLV.setAdapter(adapter);
        usersLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                int position = usersLV.getSelectedItemPosition();
                BackendObject o = adapter.getItem(position);
                Query<BackendObject> q = new QueryBuilder<BackendObject>()
                        .delete()
                        .from(BackendObject.class)
                        .where(TransientObject.OBJECT_KEY, OPERAND.EQ, o.getObjectKey())
                        .build();

                BackendServices.remote()
                        .query(BackendObject.class, q)
                        .async(new Callback<Collection<BackendObject>>() {
                            @Override
                            public void success(Collection<BackendObject> backendObjects, Response response) {
                                getObjects();
                            }

                            @Override
                            public void failure(RetrofitError retrofitError) {

                            }
                        });
        }});

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getObjects();
            }
        },1000);

        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... voids) {
                int count = 10;

                LocalStorageNoSQL noSQL = new LocalStorageNoSQL(MyActivity.this);
                noSQL.save(Arrays.asList(new AObject()));

                List<AObject> items = new ArrayList<AObject>(count);
                for(int x=0;x<count;x++){

                    AObject object = new AObject();
                    object.put("test1","test");
                    object.put("test2","test");
                    object.put("test3","test");
                    object.put("test4","test");
                    object.put("test5","test");

                    items.set(x,object);
                }

                Stopwatch timer = new Stopwatch();
                timer.start();

                BackendServices.local().save(items);
//                BackendServices.get().cache(items);

                logger.debug("Cache: " + timer.stop());

                for(int x=0;x<count;x++){
                    AObject object = items.get(x);
                    object.put("test1","test2");
                    object.put("test2","test2");
                    object.put("test3","test2");
                    object.put("test4","test2");
                    object.put("test5","test2");

                    items.set(x,object);
                }
                timer.start();
                BackendServices.local().save(items);
//                BackendServices.get().cache(items);
                logger.debug("Cache:Update: " + timer.stop());

                timer.start();
                count = BackendServices.local().getAllByType(AObject.class).size();
//                count = BackendServices.get().getCache("C1").size();
                logger.debug("Query("+count+") " + timer.stop());

                return null;
            }
        };


//        OrientTest.main(null);
    }

    @Override
    public void onResume(){
        super.onResume();
        setUser(BackendUser.getUser());
    }

    @OnClick(R.id.loginButton)
    public void login(){
        Intent intent = new Intent(MyActivity.this, AuthActivity.class);
//        intent.putExtra(AuthActivity.ARG_ACCOUNT_TYPE, AuthManager.ACCOUNT_TYPE);
        MyActivity.this.startActivity(intent);
    }

    @OnClick(R.id.clearSavedUserButton)
    public void logout(){
//        au.removeAccount(getSavedUser(au));

        BackendUser.logout();

//        GCMRegistrar.unregister(this);
        savedUserTV.setText("Cached User: ");
        loggedInUserTV.setText("User: ");
    }

    @OnClick(R.id.addObject)
    public void addObject(){

        if(user != null){
            BackendObject b = new BackendObject();
            try {
                BackendServices.remote().save(b).async( new Callback<String>() {
                    @Override
                    public void success(String s, Response response) {
                        getObjects();
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {

                    }
                });
            } catch (ObjectManager.NotLoggedInException e) {
                e.printStackTrace();
            }
        }


    }

    @OnClick(R.id.getObjects)
    public void getObjects(){
        final Query q = new QueryBuilder()
                .select()
                .from(BackendObject.class)
                .limit(10).build();

        BackendServices.remote()
                .query(BackendObject.class, q)
                .async(new Callback<Collection<BackendObject>>() {
                    @Override
                    public void success(Collection<BackendObject> objects, Response response) {
                        objectList.clear();
                        objectList.addAll(objects);
                        adapter.notifyDataSetChanged();

                        BackendServices.local().save(objects);
                        List<BackendObject> col = BackendServices.local().query(BackendObject.class, q);
                        for(BackendObject o : col)logger.debug(o);
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        retrofitError.printStackTrace();
                    }
                });

    }

    private void setUser(BackendUser user){
        if(user!=null){
//            savedUserTV.setText("Cached User: " + getSavedUser(au)); TODO replace this
            loggedInUserTV.setText("User: " + user);
            this.user = user;
        }
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
        subMenu1.add(MENU_LEVEL_1, 0, MENU_SET_URL, "Set url/ip/hostname");

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
                                app.initBackend("http://" + url + ":8888/api");
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

    private static class AObject extends BackendObject{

    }

}
