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
import io.divide.dao.Keyable;
import io.divide.shared.transitory.TransientObject;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import org.junit.After;
import org.junit.Before;

import java.util.List;

public class OrientDBDaoTest extends DAOTest<OrientDBDaoTest.KeyedODocumentWrapper> {

    ODatabaseDocument db;

    public OrientDBDaoTest() {
        super(null);
    }

    @Override
    public KeyedODocumentWrapper toBaseObject(TransientObject object) {
        return new KeyedODocumentWrapper(object);
    }

    @Override
    public void rawSave(List<KeyedODocumentWrapper> keyedWrappers) {
        for(KeyedODocumentWrapper kw: keyedWrappers){
            db.save(kw);
        }
    }

    @Before
    public void setUp() {
        db = new ODatabaseDocumentTx(OrientDBDao.DEFAULT_CONFIG);
        if(db.exists()){
            db.open("admin","admin");
        } else {
            db.create();
        }
        dao = new OrientDBDao(db);
        super.setUp();
    }

    @After
    public void tearDown() {
        db.drop();
        db.close();
    }

    public static class KeyedODocumentWrapper extends ODocumentWrapper implements Keyable{

        public KeyedODocumentWrapper(){
            super();
        }

        public KeyedODocumentWrapper(TransientObject t){
            super(t);
        }

        @Override
        public String getKey() {
            return super.getKey();
        }

//        public ODocumentWrapper getWrapper(){
//            return new ODocumentWrapper(this);
//        }
    };
}
