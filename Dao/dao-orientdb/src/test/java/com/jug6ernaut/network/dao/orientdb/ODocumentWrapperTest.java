package com.jug6ernaut.network.dao.orientdb;

import com.jug6ernaut.network.dao.DAOTest;
import com.jug6ernaut.network.dao.TestObject1;
import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.record.ODatabaseRecord;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by williamwebb on 2/16/14.
 */
public class ODocumentWrapperTest {

    ODatabase db;

    @Before
    public void setUp(){
        db = new ODatabaseDocumentTx("memory:test").create();
        ODatabaseRecordThreadLocal.INSTANCE.set((ODatabaseRecord) db);
    }

    @After
    public void tearDown(){
        db.drop();
    }

    @Test
    public void flatten(){
        TestObject1 to1 = DAOTest.testObject1;
        ODocumentWrapper wrapper = new ODocumentWrapper(to1);
        TestObject1 to2 = wrapper.toObject(TestObject1.class);
        assertEquals(to1,to2);
    }

}
