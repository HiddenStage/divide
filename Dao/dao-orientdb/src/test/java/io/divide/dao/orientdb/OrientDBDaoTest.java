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
