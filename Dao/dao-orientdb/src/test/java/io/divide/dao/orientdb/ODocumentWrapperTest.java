/*
 * Copyright (C) 2014 Divide.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.divide.dao.orientdb;

import io.divide.dao.DAOTest;
import io.divide.dao.TestObject1;
import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.record.ODatabaseRecord;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

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
