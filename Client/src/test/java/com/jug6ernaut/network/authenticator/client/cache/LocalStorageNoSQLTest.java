package com.jug6ernaut.network.authenticator.client.cache;

import android.app.Activity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jug6ernaut.android.logging.ALogger;
import com.jug6ernaut.network.authenticator.client.BackendObject;
import com.jug6ernaut.network.shared.util.ObjectUtils;
import com.jug6ernaut.network.shared.web.transitory.TransientObject;
import com.jug6ernaut.network.shared.web.transitory.query.OPERAND;
import com.jug6ernaut.network.shared.web.transitory.query.Query;
import com.jug6ernaut.network.shared.web.transitory.query.QueryBuilder;
import iBoxDB.LocalServer.BoxSystem;
import iBoxDB.LocalServer.IFunction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * Created by williamwebb on 12/24/13.
 */
@RunWith(RobolectricTestRunner.class)
@Config( shadows = {CustomSQLiteShadow.class})
public class LocalStorageNoSQLTest {
    Activity activity;
    LocalStorageNoSQL storage;

    @Before
    public void setUp() throws Exception {
        ShadowLog.stream = System.out;
        activity = Robolectric.buildActivity(Activity.class).create().get();
        ALogger.init(Robolectric.application, "dummy", true);
        storage = new LocalStorageNoSQL(activity);
    }

    @After
    public void tearDown() throws Exception {
        TestHelper.DeleteDB(activity.getFilesDir().getPath().toString());
        TestHelper.DeleteDB();
    }

    @Test
    public void testQuery() throws Exception {
        Map<String, BackendObject> map = insertTestData(storage);
//
//        long count = storage.box.selectCount("from Wrapper where Table==? && meta_data.object_key==?",B.class.getName(), map.get("b2").getObjectKey());
//        assertEquals(1,count);
//
//        Object[] args = new Object[]{B.class.getName(),map.get("b2").getObjectKey()};
//        Iterable<Wrapper> wrapper = storage.box.select(Wrapper.class, "from Wrapper where Table==? && meta_data.object_key==?",args);
//        Wrapper w = LocalStorageNoSQL.iBoxUtils.GetFrist(wrapper);
//        B b = w.toObject(B.class);
//        assertEquals(map.get("b2"), b);
//        if(!map.get("b1").equals(b) && !map.get("b2").equals(b))fail();

        Query<B> q = new QueryBuilder<B>().select().from(B.class).where(TransientObject.OBJECT_KEY, OPERAND.EQ,map.get("b1").getObjectKey()).build();

        List<B> list = storage.query(B.class, q);
        assertEquals(1, list.size());
        assertEquals(map.get("b1"), list.get(0));

        q = new QueryBuilder<B>().select().from(B.class).where(TransientObject.OBJECT_KEY, OPERAND.EQ,"nonsense").build();

        list = storage.query(B.class, q);
        assertEquals(0, list.size());
    }

    @Test
    public void testGetAllByType() throws Exception {
        Map<String, BackendObject> map = insertTestData(storage);

        List<A> list = storage.getAllByType(A.class);
        assertEquals(2, list.size());
        assertNotNull(list.get(0));
        assertNotNull(list.get(1));

        // list order is random, check against both
        A a1 = (A) map.get("a1");
        A a2 = (A) map.get("a2");

        if(!list.get(0).equals(a1) && !list.get(1).equals(a1)){
            System.err.println(list);
            System.err.println(a1);
            fail();
        }
        if(!list.get(0).equals(a2) && !list.get(1).equals(a2)){
            System.err.println(list);
            System.err.println(a2);
            fail();
        }
    }

    @Test
    public void testSave() throws Exception {
        A a = new A();
        a.put("test","test");
        storage.save(Arrays.asList(a));

        Wrapper wrapper = LocalStorageNoSQL.iBoxUtils.GetFrist(storage.box.select(Wrapper.class, "from Wrapper where Key==?", a.getObjectKey()));

        A b = wrapper.toObject(A.class);
        assertEquals(a,b);
    }

    @Test
    public void testExists() throws Exception {
        Map<String, BackendObject> map = insertTestData(storage);
        assertTrue("Does not exist",storage.exists(A.class,map.get("a1").getObjectKey()));    //right class, does exist
        assertFalse("Does not exist", storage.exists(B.class, map.get("a1").getObjectKey())); //wrong class, does exist
        assertFalse("Does not exist", storage.exists(B.class, "bad_key"));                     //doesnt exist
    }

    @Test
    public void testCount() throws Exception {
        insertTestData(storage);

        long count = storage.count(A.class);

        assertEquals(2, count);
    }

    private Map<String,BackendObject> insertTestData(LocalStorageNoSQL localStorage){
        A a1 = new A();
        a1.put("a1","a1");
        a1.put("list",Arrays.asList("1", "2", "3"));
        A a2 = new A();
        a2.put("a2","a2");
        localStorage.box.insert("Wrapper", new Wrapper(a1));
        localStorage.box.insert("Wrapper", new Wrapper(a2));

        B b1 = new B();
        b1.put("b1", "b1");
        B b2 = new B();
        b2.put("b2", "b2");
        localStorage.box.insert("Wrapper", new Wrapper(b1));
        localStorage.box.insert("Wrapper", new Wrapper(b2));

        Map<String,BackendObject> objectMap = new HashMap<String, BackendObject>();
        objectMap.put("a1",a1);
        objectMap.put("a2",a2);
        objectMap.put("b1",b1);
        objectMap.put("b2",b2);
        return objectMap;
    }

    @Test
    public void testQueryEmbededCollections() throws Exception{
        A t1 = new A();
        List<String> l1 =  Arrays.asList("2");
        t1.put("players_key",l1);

        System.out.println("embeded: " + Arrays.asList(t1));

        A t2 = new A();
        List<String> l2 = Arrays.asList("3","4","5");
        t2.put("players_key", l2);

        storage.save(Arrays.asList(t1,t2));

        Query q = new QueryBuilder().select().from(A.class).where("players_key",OPERAND.EQ,"2").build();
        List<A> results = storage.query(A.class,q);
        assertNotEquals(null, results);
        assertEquals(1, results.size());
        assertEquals(t1.getObjectKey(), ObjectUtils.get1stOrNull(results).getObjectKey());
        assertEquals(l1,ObjectUtils.get1stOrNull(results).get(l1.getClass(),"players_key"));

        q = new QueryBuilder().select().from(A.class).where("players_key",OPERAND.EQ,"4").build();
        results = storage.query(A.class,q);
        assertNotEquals(null, results);
        assertEquals(1, results.size());
        assertEquals(t2.getObjectKey(),ObjectUtils.get1stOrNull(results).getObjectKey());
        assertEquals(l2,ObjectUtils.get1stOrNull(results).get(l2.getClass(),"players_key"));

        Gson g = new GsonBuilder().create();
        g.fromJson(g.toJson(t1),TransientObject.class);
    }

//    @Test
//    public void testComplextObjects() throws Exception {
//        A a = new A();
//        List<Object> list = new ArrayList<Object>();
//        list.add(Arrays.asList(1,"2",3.0));
//        Map map = new HashMap();
//        map.put("1","1");
//        map.put(2,2);
//        map.put(3.0,3.0);
//        list.add(map);
//        a.put("list", list);
//        a.put("map",map);
//
//        storage.save(Arrays.asList(a));
//
//        Wrapper wrapper = LocalStorageNoSQL.iBoxUtils.GetFrist(storage.box.select(Wrapper.class, "from Wrapper where Key==?", a.getObjectKey()));
//
//        A b = wrapper.toObject(A.class);
//        assertEquals(a,b);
//    }

    private static class A extends BackendObject {

        protected <T extends TransientObject> A() {
            super(A.class);
        }
    }

    private static class B extends BackendObject{

        protected <T extends TransientObject> B() {
            super(B.class);
        }
    }

    private static class TestHelper {
        public static void DeleteDB() {
            if (!BoxSystem.DBDebug.DeleteDBFiles(1)) {
                System.out.println("delete=false,system locks");
            }
        }

        public static void DeleteDB(String url) {
            if (!BoxSystem.DBDebug.DeleteDBFiles(url)) {
                System.out.println("delete=false,system locks");
            }
        }
    }

    public static class QueryArray implements IFunction {
        String match;

        public QueryArray(String match) {
            this.match = match;
        }

        public Object execute(int argCount, Object[] args) {
            Object[] tags = (Object[]) args[0];
            if (tags == null) {
                return false;
            }
            for (Object t : tags) {
                if (match.equals(t)) {
                    return true;
                }
            }
            return false;
        }
    }

    public static class QueryMap implements IFunction {
        String match;

        public QueryMap(String match) {
            this.match = match;
        }

        public Object execute(int argCount, Object[] args) {
            Map tags = (Map) args[0];
            if (tags == null) {
                return false;
            }
            for(Object e : tags.values()){
                if (match.equals(e)) {
                    return true;
                }
            }
            return false;
        }
    }


}
