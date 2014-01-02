package com.jug6ernaut.network.authenticator.client.cache;

import android.app.Activity;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.util.Log;
import com.jug6ernaut.android.logging.ALogger;
import com.jug6ernaut.network.authenticator.client.BackendObject;
import com.jug6ernaut.network.shared.util.ObjectUtils;
import com.jug6ernaut.network.shared.web.transitory.TransientObject;
import com.jug6ernaut.network.shared.web.transitory.query.Query;
import com.jug6ernaut.network.shared.web.transitory.query.QueryBuilder;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import java.util.*;

/**
* Created by williamwebb on 12/6/13.
*/
@RunWith(RobolectricTestRunner.class)
@Config( shadows = {CustomSQLiteShadow.class})
public class LocalStorageSQLiteTest extends TestCase {

    LocalStorageSQLite storage;
    DatabaseInitializer di;

    @Before
    public void setUp() throws Exception {
        ShadowLog.stream = System.out;
        Activity activity = Robolectric.buildActivity(Activity.class).create().get();
        ALogger.init(Robolectric.application, "dummy", true);

        di = new DatabaseInitializer(Robolectric.application);

        storage = new LocalStorageSQLite(activity);
        storage.setDBHelper(di);
    }

    @Test
    public void testSave() throws Exception {
        AObject a = new AObject();
        a.put("test","test");
        a.put("collection",Arrays.asList(1,2,3));
        storage.save(Arrays.asList(a));

        Cursor c = di.getDB().rawQuery("SELECT * FROM " + LocalStorageSQLite.getCodedTableName(a.getClass()) + " WHERE " + LocalStorageSQLite.META_TAG + "object_key='"+a.getObjectKey()+"'",null);
        c.moveToFirst();
//        fail(DatabaseUtils.dumpCurrentRowToString(c));
        assertTrue(DatabaseUtils.dumpCurrentRowToString(c), c.moveToFirst());
        assertTrue("Wrong number of rows " + c.getCount(), c.getCount() == 1);
        assertTrue(DatabaseUtils.dumpCurrentRowToString(c),c.getString(c.getColumnIndex(LocalStorageSQLite.META_TAG + "object_key")).equals(a.getObjectKey()));
        assertTrue(DatabaseUtils.dumpCurrentRowToString(c),c.getString(c.getColumnIndex(LocalStorageSQLite.USER_TAG + "test")).equals(a.get(String.class,"test")));

        a.put("test2","test2");
        storage.save(Arrays.asList(a));

        c = di.getDB().rawQuery("SELECT * FROM " + LocalStorageSQLite.getCodedTableName(a.getClass()) + " WHERE " + LocalStorageSQLite.META_TAG + "object_key='"+a.getObjectKey()+"'",null);
        c.moveToFirst();
        assertTrue(DatabaseUtils.dumpCurrentRowToString(c), c.moveToFirst());
        assertTrue("Wrong number of rows " + c.getCount(), c.getCount() == 1);
        assertTrue(DatabaseUtils.dumpCurrentRowToString(c),c.getString(c.getColumnIndex(LocalStorageSQLite.META_TAG + "object_key")).equals(a.getObjectKey()));
        assertTrue(DatabaseUtils.dumpCurrentRowToString(c),c.getString(c.getColumnIndex(LocalStorageSQLite.USER_TAG + "test2")).equals(a.get(String.class,"test2")));

        AObject b = new AObject();
        storage.save(Arrays.asList(b));

        c = di.getDB().rawQuery("SELECT * FROM " + LocalStorageSQLite.getCodedTableName(b.getClass()) + " WHERE " + LocalStorageSQLite.META_TAG + "object_key='"+b.getObjectKey()+"'",null);
        c.moveToFirst();
        assertTrue(DatabaseUtils.dumpCurrentRowToString(c), c.moveToFirst());
        assertTrue("Wrong number of rows " + c.getCount(), c.getCount() == 1);
        assertTrue(DatabaseUtils.dumpCurrentRowToString(c),c.getString(c.getColumnIndex(LocalStorageSQLite.META_TAG + "object_key")).equals(b.getObjectKey()));

        c = di.getDB().rawQuery("SELECT * FROM " + LocalStorageSQLite.getCodedTableName(b.getClass()),null);
        assertTrue("Wrong number of rows " + c.getCount(), c.getCount() == 2);
    }

    @Test
    public void testExists() throws Exception {
        AObject a = new AObject();
        a.put("test","test");
        storage.save(Arrays.asList(a));
        assertTrue(storage.exists(AObject.class,a.getObjectKey()));
    }

    @Test
    public void testCount() throws Exception {
        AObject a = new AObject();
        a.put("test","test");
        storage.save(Arrays.asList(a));
        assertTrue(storage.count(AObject.class) == 1);

        AObject b = new AObject();
        storage.save(Arrays.asList(b));
        assertTrue(storage.count(AObject.class) == 2);

        BObject c = new BObject();
        storage.save(Arrays.asList(c));
        assertTrue(storage.count(AObject.class) == 2);
        assertTrue(storage.count(BObject.class) == 1);
    }

    @Test
    public void testGetAllByType() throws Exception {
        AObject a = new AObject();
        AObject b = new AObject();
        BObject c = new BObject();

        storage.save(Arrays.asList(a,b,c));

        List<AObject> aList = storage.getAllByType(AObject.class);
        assertTrue(aList.size() == 2);
        List<BObject> bList = storage.getAllByType(BObject.class);
        assertTrue(bList.size() == 1);

        BObject bObject = ObjectUtils.get1stOrNull(bList);
        assertTrue(c.toString().equals(bObject.toString()));
    }

    @Test
    public void testQuery() throws Exception {
        AObject a = new AObject(); a.put("value1","1");
        AObject b = new AObject(); b.put("value2","2");
        BObject c = new BObject(); c.put("value3", "3");

        storage.save(Arrays.asList(a,b,c));

        Query<BObject> q = new QueryBuilder<BObject>().select().from(BObject.class).build();
        List<BObject> list = storage.query(BObject.class,q);
        assertTrue(list.size() == 1);
        BObject bObject = ObjectUtils.get1stOrNull(list);
        Log.e("dummy", "C: " + c);
        Log.e("dummy", "C: " + bObject);
        assertTrue(bObject.toString(), bObject.toString().equals(c.toString()));
    }

    @Test
    public void testComplextObjects() throws Exception {
        AObject a = new AObject();
        List<Object> list = new ArrayList<Object>();
        list.add(Arrays.asList(1,"2",3.0));
        Map map = new HashMap();
        map.put("1","1");
        map.put(2,2);
        map.put(3.0,3.0);
        list.add(map);
        a.put("list", list);
        a.put("map",map);


        storage.save(Arrays.asList(a));
    }

    private static class AObject extends BackendObject{

        protected <T extends TransientObject> AObject() {
            super(AObject.class);
        }
    }

    private static class BObject extends BackendObject{

        protected <T extends TransientObject> BObject() {
            super(AObject.class);
        }
    }
}
