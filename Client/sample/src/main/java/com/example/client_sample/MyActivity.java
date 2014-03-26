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
import io.divide.client.BackendObject;
import io.divide.client.BackendServices;
import io.divide.client.BackendUser;
import io.divide.client.auth.AuthActivity;
import io.divide.client.auth.LoginEvent;
import io.divide.client.auth.LoginListener;
import io.divide.client.auth.LoginState;
import io.divide.shared.web.transitory.TransientObject;
import io.divide.shared.web.transitory.query.OPERAND;
import io.divide.shared.web.transitory.query.Query;
import io.divide.shared.web.transitory.query.QueryBuilder;
import rx.util.functions.Action1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MyActivity extends Activity {

    private Logger logger = Logger.getLogger(MyActivity.class);
    private MyApplication app;
    private List<BackendObject> objectList = new ArrayList<BackendObject>();
    private BackendObjectAdaper adapter;
    private BackendUser user;

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

        BackendServices.addLoginListener(new LoginListener(){

            @Override
            public void onNext(LoginEvent loginEvent) {
                if(LoginState.LOGGED_IN.equals(loginEvent.state)){
                    logger.info("loginListener: setUser: " + loginEvent.user);
                    setUser(user);
                }
            }
        });

        adapter = new BackendObjectAdaper(this,objectList);
        usersLV.setAdapter(adapter);
        usersLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BackendObject o = adapter.getItem(position);
                Query q = new QueryBuilder()
                        .delete()
                        .from(BackendObject.class)
                        .where(TransientObject.OBJECT_KEY, OPERAND.EQ, o.getObjectKey())
                        .build();

                BackendServices.remote()
                        .query(BackendObject.class, q)
                        .subscribe(new Action1<Collection<BackendObject>>() {
                            @Override
                            public void call(Collection<BackendObject> backendObjects) {
                                getObjects();
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

            BackendServices
                    .remote()
                    .save(new BackendObject())
                    .subscribe(new Action1<String>() {
                        @Override
                        public void call(String s) {
                            getObjects();
                        }
                    });

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
                .subscribe(new Action1<Collection<BackendObject>>() {
                               @Override
                               public void call(Collection<BackendObject> objects) {
                                   objectList.clear();
                                   objectList.addAll(objects);
                                   adapter.notifyDataSetChanged();

                                   BackendServices.local().save(objects);
                                   List<BackendObject> col = BackendServices.local().query(BackendObject.class, q);
                                   for (BackendObject o : col) logger.debug(o);
                               }
                           }, new Action1<Throwable>() {
                               @Override
                               public void call(Throwable throwable) {
                                   logger.debug("",throwable);
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
