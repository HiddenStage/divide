package com.jug6ernaut.network.authenticator.client;

import com.jug6ernaut.network.authenticator.client.auth.AuthManager;
import com.jug6ernaut.network.authenticator.client.data.DataManager;
import com.jug6ernaut.network.authenticator.client.push.PushManager;
import com.jug6ernaut.network.shared.web.transitory.query.Query;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 8/29/13
 * Time: 9:42 PM
 */
public class DataServices {

    private static DataServices dataService;
    private DataManager dataManager;
    private AuthManager authManager;
    private PushManager pushManager;

    private DataServices(){
        Backend b = Backend.get();
        dataManager = b.getDataManager();
        authManager = b.getAuthManager();
        pushManager = b.getPushManager();
    }

    public static synchronized DataServices get(){
        if(dataService == null)dataService = new DataServices();
        return dataService;
    }

    public BackendUser getUser(){
        return authManager.getUser();
    }

    public BackendUser signUp(BackendUser user) throws Exception{
        return authManager.signUp(user.getEmailAddress(), user.getUsername(),user.getPassword());
    }

    public BackendUser login(String username, String password) throws Exception{
        return authManager.login(username,password);
    }

    public void logout(){
        authManager.logout();
    }

    public void send(BackendObject... objects){
        dataManager.send(assignUser(objects));
    }

    public Collection<BackendObject> get(BackendObject... objects){
        return dataManager.get(assignUser(objects));
    }

    public Collection<BackendObject> query(Query query){
        return dataManager.query(query);
    }

    public void query(Query query, retrofit.Callback<Collection<BackendObject>> callback){
        dataManager.query(query,callback);
    }

    public void addLoginListener(AuthManager.LoginListener loginListener){
        authManager.addLoginListener(loginListener);
    }

    public void removeLoginListener(AuthManager.LoginListener loginListener){
        authManager.removeLoginListener(loginListener);
    }

    private Collection<BackendObject> assignUser(BackendObject... objects){
        String userId = authManager.getUser().getUserId();
        for (BackendObject object : objects){
            object.setUserId(userId);
        }
        return Arrays.asList(objects);
    }

    public void setPushToken(String token){
        pushManager.register(token);
    }
}
