package io.divide.dao.orientdb;

import io.divide.dao.DAOTest;
import io.divide.dao.Keyable;
import io.divide.shared.web.transitory.TransientObject;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import org.junit.After;
import org.junit.Before;

import java.util.List;

/**
* Created by williamwebb on 2/14/14.
*/
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
