package com.example.client_sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.jug6ernaut.android.logging.Logger;
import com.jug6ernaut.network.authenticator.client.BackendObject;
import com.jug6ernaut.network.authenticator.client.BackendServices;
import com.jug6ernaut.network.authenticator.client.BackendUser;
import com.jug6ernaut.network.authenticator.client.auth.AuthActivity;
import com.jug6ernaut.network.authenticator.client.auth.AuthManager;
import com.jug6ernaut.network.authenticator.client.auth.LoginState;
import com.jug6ernaut.network.authenticator.client.data.ObjectManager;
import com.jug6ernaut.network.shared.web.transitory.TransientObject;
import com.jug6ernaut.network.shared.web.transitory.query.OPERAND;
import com.jug6ernaut.network.shared.web.transitory.query.Query;
import com.jug6ernaut.network.shared.web.transitory.query.QueryBuilder;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MyActivity extends Activity {

    private Logger logger = Logger.getLogger(MyActivity.class);
    private MyApplication app;
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
        getActionBar().setHomeButtonEnabled(true);

//        au = AuthUtils.get(this, AuthManager.ACCOUNT_TYPE); TODO replace this
        app = (MyApplication) this.getApplication();

        urlTV.setText("URL: " + app.getUrl(app.getSharedPreferences()));

        BackendServices.addLoginListener(new AuthManager.LoginListener() {
            @Override
            public void onLogin(final BackendUser user, LoginState state) {
                if(LoginState.LOGGED_IN.equals(state))
                MyActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        logger.info("loginListener: setUser: " + user);
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
    }

    @Override
    public void onResume(){
        super.onResume();
        setUser(BackendUser.getUser());
    }

    @OnClick(R.id.loginButton)
    public void login(){
        Intent intent = new Intent(MyActivity.this, AuthActivity.class);
        MyActivity.this.startActivity(intent);
    }

    @OnClick(R.id.clearSavedUserButton)
    public void logout(){
        BackendUser.logout();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home: {
                Toast.makeText(this,"Reinitializing...",Toast.LENGTH_LONG).show();
                app.reinitialize();
                setUser(BackendUser.getUser());
                return false;
            }
            default:{
                return super.onOptionsItemSelected(item);
            }
        }
    }

}
