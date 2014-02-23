package com.jug6ernaut.network.dao.orientdb;

import com.jug6ernaut.network.dao.DAOTest;
import com.jug6ernaut.network.dao.Keyable;
import com.jug6ernaut.network.shared.web.transitory.TransientObject;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;

import java.util.List;

/**
* Created by williamwebb on 2/14/14.
*/
public class OrientDBDaoTest extends DAOTest<OrientDBDaoTest.KeyedODocumentWrapper> {

    ODatabaseDocument db;

    public OrientDBDaoTest() {
        super(new OrientDBDao(new ODatabaseDocumentTx("memory:unique!").<ODatabaseDocument>create()));
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

    @Override
    public void setUp() {
        db = new ODatabaseDocumentTx("memory:unique!").create();
        dao = new OrientDBDao(db);
    }

    @Override
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
