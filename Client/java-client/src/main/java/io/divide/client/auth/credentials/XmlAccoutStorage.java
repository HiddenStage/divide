package io.divide.client.auth.credentials;

import com.google.gson.Gson;
import io.divide.client.auth.AccountStorage;
import io.divide.shared.file.Storage;
import io.divide.shared.file.XmlStorage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by williamwebb on 4/15/14.
 */
public class XmlAccoutStorage implements AccountStorage {

    Gson gson = new Gson();
    XmlStorage storage;

    public XmlAccoutStorage(File file){
        this.storage = new XmlStorage(file, Storage.MODE_WORLD_WRITEABLE);
    }

    @Override
    public void addAcccount(LocalCredentials credentials) {
        storage.edit().putString(credentials.getName(),gson.toJson(credentials)).commit();
    }

    @Override
    public void removeAccount(String accountName) {
        storage.edit().remove(accountName).commit();
    }

    @Override
    public LocalCredentials getAccount(String accountName) {
        if(storage.contains(accountName))
            return gson.fromJson(storage.getString(accountName,null),LocalCredentials.class);
        else
            return null;
    }

    @Override
    public boolean isAuthenticated(String accountName) {
        return false;
    }

    @Override
    public void setAuthToken(String accountName, String token) {
        LocalCredentials credentials = getAccount(accountName);
        if(credentials!=null){
            credentials.setAuthToken(token);
            addAcccount(credentials);
        }
    }

    @Override
    public void setRecoveryToken(String accountName, String token) {
        LocalCredentials credentials = getAccount(accountName);
        if(credentials!=null){
            credentials.setRecoveryToken(token);
            addAcccount(credentials);
        }
    }

    @Override
    public List<LocalCredentials> getAccounts() {
        Map<String, String> map = (Map<String, String>) storage.getAll();
        List<LocalCredentials> list = new ArrayList<LocalCredentials>(map.size());

        for(String s : map.values()){
            list.add(gson.fromJson(s,LocalCredentials.class));
        }

        return list;
    }

    @Override
    public boolean exists(String name) {
        return storage.contains(name);
    }
}
