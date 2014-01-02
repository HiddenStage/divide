package com.jug6ernaut.network.authenticator.server.appengine;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.googlecode.objectify.ObjectifyService;
import com.jug6ernaut.network.shared.util.ObjectUtils;
import com.jug6ernaut.network.shared.web.transitory.TransientObject;
import com.jug6ernaut.network.shared.web.transitory.query.OPERAND;
import com.jug6ernaut.network.shared.web.transitory.query.Query;
import com.jug6ernaut.network.shared.web.transitory.query.QueryBuilder;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.googlecode.objectify.ObjectifyService.ofy;
import static org.junit.Assert.*;

/**
 * Created by williamwebb on 11/16/13.
 */
public class ObjectifyDAOTest {

    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    ObjectifyDAO dao = new ObjectifyDAO();
    static TestObject1 testObject1 = new TestObject1();
    static OfyObject object1;
    static {
        testObject1.put("key1", "1");
        testObject1.put("key2","value2");
        testObject1.put("key3","value3");
        try {
            object1 = BackendToOfy.getOfy(testObject1);
        } catch (Exception e){}
    }
    static TestObject1 testObject2 = new TestObject1();
    static OfyObject object2 = new OfyObject();
    static {
        testObject2.put("key1", "1");
        testObject2.put("key2", "value2");
        testObject2.put("key3", "value3");
        try {
            object2 = BackendToOfy.getOfy(testObject2);
        } catch (Exception e){}
    }
    static TestObject1 testObject4 = new TestObject1();
    static OfyObject object4 = new OfyObject();
    static {
        testObject4.put("key1", "3");
        testObject4.put("key2", "value2");
        testObject4.put("key3", "value3");
        try {
            object4 = BackendToOfy.getOfy(testObject4);
        } catch (Exception e){}
    }
    static TestObject2 testObject3 = new TestObject2();
    static OfyObject object3 = new OfyObject();
    static {
        testObject3.put("key1", "1");
        testObject3.put("key2", "2");
        testObject3.put("key3", "value3");
        try {
            object3 = BackendToOfy.getOfy(testObject3);
        } catch (Exception e){}
    }

    public ObjectifyDAOTest(){
        ObjectifyService.register(OfyObject.class);
    }

    @Before
    public void setUp() {
        helper.setUp();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void testQuery() throws Exception {
        ofy().save().entities(object1,object2,object3,object4).now();

        Query q;
        List<TransientObject> results;

        String key = testObject1.getObjectKey();
        //where
        q = new QueryBuilder().select().from(TestObject1.class).where(TransientObject.OBJECT_KEY, OPERAND.EQ,key).build();
        results = dao.query(q);
        assertFalse("1.", results == null);
        assertFalse("2.", results.size()!=1);
        assertFalse("3.", !ObjectUtils.get1stOrNull(results).getObjectKey().equals(key));

        q = new QueryBuilder().select().from(TestObject1.class).build();
        results = dao.query(q);
        assertNotEquals("Select All", null, results);
        assertEquals("Select All", 3, results.size());

        q = new QueryBuilder().select().from(TestObject1.class).where("key1",OPERAND.EQ,"1").build();
        results = dao.query(q);
        assertNotEquals("key1=", null, results);
        assertEquals("key1=", 2, results.size());

        q = new QueryBuilder().select().from(TestObject1.class).where("key1",OPERAND.GREATER_THAN,"1").build();
        results = dao.query(q);
        assertNotEquals("key1>", null, results);
        assertEquals("key1>", 1, results.size());

        q = new QueryBuilder().select().from(TestObject1.class).where("key1",OPERAND.GREATER_THAN_EQ,"1").build();
        results = dao.query(q);
        assertNotEquals("key1>=", null, results);
        assertEquals("key1>=", 3, results.size());

        q = new QueryBuilder().select().from(TestObject1.class).where("key1",OPERAND.LESS_THAN,"3").build();
        results = dao.query(q);
        assertNotEquals("key1<", null, results);
        assertEquals("key1<", 2, results.size());

        q = new QueryBuilder().select().from(TestObject1.class).where("key1",OPERAND.LESS_THAN_EQ,"3").build();
        results = dao.query(q);
        assertNotEquals("key1<=", null, results);
        assertEquals("key1<=", 3, results.size());

        q = new QueryBuilder().select().from(TestObject2.class)
                .where("key1",OPERAND.EQ,"1")
                .where("key2",OPERAND.EQ,"2").build();
        results = dao.query(q);
        assertNotEquals("2where", null, results);
        assertEquals("2where", 1, results.size());
        assertEquals("2where", testObject3.getObjectKey(), ObjectUtils.get1stOrNull(results).getObjectKey());

        //limit
        q = new QueryBuilder().select().from(TestObject1.class).where("key1",OPERAND.LESS_THAN_EQ,"3").limit(2).build();
        results = dao.query(q);
        assertNotEquals("limit", null, results);
        assertEquals("limit", 2, results.size());

        //offset
    }

//    [TransientObject{
//        user_data={players_key=[2]}
//        , meta_data={object_type=com.jug6ernaut.tactics.client.web.WGame, object_key=049d74c7-511c-49ef-a312-3a782f2f41e3, create_date_key=1384732193542, modified_date_key=1384732193543}
//    }]
//    [TestObject1{
//        user_data={players_key=[2]}
//        , meta_data={object_type=com.jug6ernaut.network.authenticator.se, create_date_key=1384733366530, object_key=e842d295-cee8-45da-83df-d235f5b1801f, modified_date_key=1384733366530}
//    }]

    @Test
    public void testQueryEmbededCollections() throws Exception{
        TestObject1 t1 = new TestObject1();
        List<String> l1 =  Arrays.asList("2");
        t1.put("players_key",l1);

        System.out.println("embeded: " + Arrays.asList(t1));

        TestObject1 t2 = new TestObject1();
        List<String> l2 = Arrays.asList("3","4","5");
        t2.put("players_key", l2);

        dao.save(t1,t2);

        Query q = new QueryBuilder().select().from(TestObject1.class).where("players_key",OPERAND.EQ,"2").build();
        List<TransientObject> results = dao.query(q);
        assertNotEquals(null, results);
        assertEquals(1, results.size());
        assertEquals(t1.getObjectKey(),ObjectUtils.get1stOrNull(results).getObjectKey());
        assertEquals(l1,ObjectUtils.get1stOrNull(results).get(l1.getClass(),"players_key"));

        q = new QueryBuilder().select().from(TestObject1.class).where("players_key",OPERAND.EQ,"4").build();
        results = dao.query(q);
        assertNotEquals(null, results);
        assertEquals(1, results.size());
        assertEquals(t2.getObjectKey(),ObjectUtils.get1stOrNull(results).getObjectKey());
        assertEquals(l2,ObjectUtils.get1stOrNull(results).get(l2.getClass(),"players_key"));

        Gson g = new GsonBuilder().create();
        g.fromJson(g.toJson(t1),TransientObject.class);
    }

    @Test
    public void testGet() throws Exception {
        ofy().save().entities(object1,object2,object3,object4).now();
        String[] keys = {object1.object_key,object3.object_key};

        Collection<TransientObject> results = dao.get(keys);
        assertNotEquals(null, results);
        assertEquals(2, results.size());

        String[] keys2 = {object1.object_key,object2.object_key,object3.object_key,object4.object_key};
        results = dao.get(keys2);
        assertNotEquals(null, results);
        assertEquals(4, results.size());
    }

    @Test
    public void testSave() throws Exception {
        ofy().save().entities(object1,object2).now();
        Collection<TransientObject> results = dao.get(testObject1.getObjectKey(),testObject2.getObjectKey());
        assertNotEquals(null, results);
        assertEquals(2, results.size());

        String bigString = StringUtils.leftPad("", 501, "0");
        testObject1.put("big_string", bigString);
        dao.save(testObject1);
    }

    @Test
    public void testDelete() throws Exception {
        ofy().save().entities(object1,object2,object4).now();
        dao.delete(testObject1,testObject2);

        Collection<TransientObject> results = dao.get(testObject1.getObjectKey(),testObject2.getObjectKey());
        assertNotEquals(null, results);
        assertEquals(0, results.size());

        Query q = new QueryBuilder().select().from(TestObject1.class).build();
        results = dao.query(q);
        assertNotEquals("Select All", null, results);
        assertEquals("Select All", 1, results.size());
    }

    @Test
    public void testExists() throws Exception {
        ofy().save().entities(object1,object2).now();

        boolean exists = dao.exists(testObject1, testObject2);
        assertTrue(exists);

        exists = dao.exists(testObject1);
        assertTrue(exists);

        exists = dao.exists(testObject3);
        assertFalse(exists);
    }

    @Test
    public void testCount() throws Exception {
        ofy().save().entities(object1,object2,object3,object4).now();

        int count = dao.count(TestObject1.class.getName());
        assertEquals(3, count);

        count = dao.count(TestObject2.class.getName());
        assertEquals(1, count);

        count = dao.count(TransientObject.class.getName());
        assertEquals(0, count);
    }

}
