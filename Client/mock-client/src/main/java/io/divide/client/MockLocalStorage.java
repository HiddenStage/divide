package io.divide.client;

import com.google.inject.Inject;
import io.divide.client.cache.LocalStorageIBoxDb;
import io.divide.shared.transitory.TransientObject;

import java.io.File;

/**
 * Created by williamwebb on 4/12/14.
 */
public class MockLocalStorage <T1 extends TransientObject,T2 extends TransientObject> extends LocalStorageIBoxDb<T1,T2> {

    @Inject
    public MockLocalStorage(Config config){
        super(getPath(config.fileSavePath));
    }

    private static String getPath(String path){
        File f = new File(path + (path.length()>0?"/":"") + getCount());
        f.mkdirs();
        return f.getPath();
    }

    private static int count=0;
    private static synchronized int getCount(){
        return count++;
    }
}
