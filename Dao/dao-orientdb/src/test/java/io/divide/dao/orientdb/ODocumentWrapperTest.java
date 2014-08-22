/*
 * Copyright (C) 2014 Divide.io
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
