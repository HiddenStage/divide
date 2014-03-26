package io.divide.dao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.divide.shared.util.ObjectUtils;
import io.divide.shared.web.transitory.TransientObject;
import io.divide.shared.web.transitory.query.OPERAND;
import io.divide.shared.web.transitory.query.Query;
import io.divide.shared.web.transitory.query.QueryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

/**
* Created by williamwebb on 2/14/14.
*/
public abstract class DAOTest<BaseObject extends Keyable> {

    protected DAO dao;
    public static final TestObject1 testObject1 = new TestObject1("key1","1",
                                              "key2","value2",
                                              "key3","value3");
    public static final TestObject1 testObject2 = new TestObject1("key1","3",
                                              "key2","value2",
                                              "key3","value3");
    public static final TestObject1 testObject3 = new TestObject1("key1","1",
                                              "key2","2",
                                              "key3","value3");
    public static final TestObject2 testObject4 = new TestObject2("key1","1",
                                              "key2","2",
                                              "key3","value3");

    BaseObject object1;
    BaseObject object2;
    BaseObject object3;
    BaseObject object4;

    String testObject1Table = Query.safeTable(TestObject1.class);
    String testObject2Table = Query.safeTable(TestObject2.class);


    protected DAOTest(DAO dao){
        this.dao = dao;
    }

    @Before
    public void setUp(){
        object1 = toBaseObject(testObject1);
        object2 = toBaseObject(testObject2);
        object3 = toBaseObject(testObject3);
        object4 = toBaseObject(testObject4);
    }

    @After
    public void tearDown(){};

    @Test
    public void testQuery() throws Exception {
        rawSave(object1,object2,object3,object4);

        Query q;
        List<TransientObject> results;

        String key = testObject1.getObjectKey();
        //where
        q = new QueryBuilder().select().from(TestObject1.class).where(TransientObject.OBJECT_KEY, OPERAND.EQ,key).build();
        results = dao.query(q);
        assertFalse("1.", results == null);
        assertTrue("2.", results.size()==1);
        TransientObject to = ObjectUtils.get1stOrNull(results);
        assertEquals(results+"", to.getObjectKey(), testObject1.getObjectKey());

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
                .or("key2",OPERAND.EQ,"2").build();
        results = dao.query(q);
        assertNotEquals("2where", null, results);
        assertEquals("2where", 1, results.size());
        assertEquals("2where", testObject4.getObjectKey(), ObjectUtils.get1stOrNull(results).getObjectKey());

        //limit
        q = new QueryBuilder().select().from(TestObject1.class).where("key1",OPERAND.LESS_THAN_EQ,"3").limit(2).build();
        results = dao.query(q);
        assertNotEquals("limit", null, results);
        assertEquals("limit", 2, results.size());

        q = new QueryBuilder().delete().from(TestObject1.class).where("key1",OPERAND.LESS_THAN_EQ,"3").build();
        results = dao.query(q);
        assertNotEquals("delete", null, results);
        assertEquals("delete", 1, results.size());
        assertEquals("delete", 3, (int)results.get(0).get(Integer.class,"count"));

        q = new QueryBuilder().delete().from(TestObject1.class).build();
        results = dao.query(q);
        assertNotEquals("delete", null, results);
        assertEquals("delete", 1, results.size());
        assertEquals("delete", 0, (int)results.get(0).get(Integer.class,"count"));

        //offset
    }

//    [TransientObject{
//        user_data={players_key=[2]}
//        , meta_data={object_type=com.jug6ernaut.tactics.client.web.WGame, getKey()=049d74c7-511c-49ef-a312-3a782f2f41e3, create_date_key=1384732193542, modified_date_key=1384732193543}
//    }]
//    [TestObject1{
//        user_data={players_key=[2]}
//        , meta_data={object_type=io.divide.authenticator.se, create_date_key=1384733366530, getKey()=e842d295-cee8-45da-83df-d235f5b1801f, modified_date_key=1384733366530}
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

        Query q = new QueryBuilder().select().from(TestObject1.class).where("players_key",OPERAND.CONTAINS,"2").build();
        List<TransientObject> results = dao.query(q);
        assertNotEquals(null, results);
        assertEquals(1, results.size());
        assertEquals(t1.getObjectKey(),ObjectUtils.get1stOrNull(results).getObjectKey());
        assertEquals(l1,ObjectUtils.get1stOrNull(results).get(l1.getClass(),"players_key"));

        q = new QueryBuilder().select().from(TestObject1.class).where("players_key",OPERAND.CONTAINS,"4").build();
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
        rawSave(object1,object2,object3,object4);
        String[] keys = {object1.getKey(),object3.getKey()};

        Collection<TransientObject> results = dao.get(testObject1Table, keys);
        assertNotEquals(null, results);
        assertEquals(String.valueOf(results), 2, results.size());

        String[] keys2 = {object1.getKey(),object2.getKey(),object3.getKey(),object4.getKey()};
        results = dao.get(testObject2Table, keys2);
        assertNotEquals(null, results);
        assertEquals(results+"", 1, results.size());
    }

    @Test
    public void testSave() throws Exception {
        dao.save(testObject1,testObject2);
        String testObject1Table = Query.safeTable(TestObject1.class);
        Collection<TransientObject> results = dao.get(testObject1Table, testObject1.getObjectKey(),testObject2.getObjectKey());
        assertNotEquals(null, results);
        assertEquals(String.valueOf(results),2, results.size());
        assertEquals(null,testObject1.get(String.class,"someKey"));

        testObject1.put("someKey","someValue");
        dao.save(testObject1);
        Collection<TransientObject> objects = dao.get(testObject1Table, testObject1.getObjectKey()); //  dao.query(new QueryBuilder().select().from(TestObject1.class).where(TransientObject.OBJECT_KEY, OPERAND.EQ, testObject1.getObjectKey()).build());
        assertEquals("Returned wrong number of objects",1,objects.size());
        TransientObject o = ObjectUtils.get1stOrNull(objects);
        assertEquals("Value not updated","someValue",o.get(String.class,"someKey"));

        String bigString = CharBuffer.allocate(600).toString();
        testObject1.put("big_string", bigString);
        dao.save(testObject1);
    }

    @Test
    public void testDelete() throws Exception {
        rawSave(object1,object2,object3,object4);
        dao.delete(testObject1);
        dao.delete(testObject4);

        Collection<TransientObject> results = dao.get(testObject1Table,testObject1.getObjectKey(),testObject2.getObjectKey());
        assertNotEquals(null, results);
        assertEquals(1, results.size());
        assertEquals(ObjectUtils.get1stOrNull(results).getObjectKey(),testObject2.getObjectKey());

        Query q = new QueryBuilder().select().from(TestObject1.class).build();
        results = dao.query(q);
        assertNotEquals("Select All", null, results);
        assertEquals("Select All", 2, results.size());

        q = new QueryBuilder().select().from(TestObject2.class).build();
        results = dao.query(q);
        assertNotEquals("Select All", null, results);
        assertEquals("Select All", 0, results.size());
    }

    @Test
    public void testExists() throws Exception {
        rawSave(object1,object2);

        boolean exists = dao.exists(testObject1, testObject2);
        assertTrue(exists);

        exists = dao.exists(testObject1);
        assertTrue(exists);

        exists = dao.exists(testObject3);
        assertEquals(testObject3.getObjectKey(),false,exists);

    }

    @Test
    public void testCount() throws Exception {
        rawSave(object1,object2,object3,object4);

        int count = dao.count(testObject1Table);
        assertEquals(3, count);

        count = dao.count(testObject2Table);
        assertEquals(1, count);

        count = dao.count(Query.safeTable(TransientObject.class));
        assertEquals(0, count);
    }

    public abstract BaseObject toBaseObject(TransientObject object);
    public abstract void rawSave(List<BaseObject> objects);

    private void rawSave(BaseObject... objects){
        rawSave(Arrays.asList(objects));
    }

}
