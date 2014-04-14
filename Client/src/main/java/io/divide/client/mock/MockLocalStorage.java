package io.divide.client.mock;

import android.content.Context;
import com.google.inject.Inject;
import io.divide.client.cache.LocalStorageIBoxDb;
import io.divide.shared.web.transitory.TransientObject;

import java.io.File;

/**
 * Created by williamwebb on 4/12/14.
 */
public class MockLocalStorage <T1 extends TransientObject,T2 extends TransientObject> extends LocalStorageIBoxDb<T1,T2> {

    @Inject
    public MockLocalStorage(Context context){
        super(getPath(context));
    }

    private static String getPath(Context context){
        File f = new File(context.getFilesDir().getPath() + "/" + getCount());
        f.mkdirs();
        return f.getPath();
    }

    private static int count=0;
    private static synchronized int getCount(){
        return count++;
    }
}
