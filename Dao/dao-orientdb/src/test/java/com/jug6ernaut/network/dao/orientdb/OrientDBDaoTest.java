//package com.jug6ernaut.network.dao.orientdb;
//
//import com.jug6ernaut.network.dao.DAOTest;
//import com.jug6ernaut.network.dao.Keyable;
//import com.jug6ernaut.network.shared.web.transitory.TransientObject;
//import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
//
//import java.util.List;
//
///**
// * Created by williamwebb on 2/14/14.
// */
//public class OrientDBDaoTest extends DAOTest<OrientDBDaoTest.KeyedWrapper> {
//
//    public OrientDBDaoTest() {
//        super(new OrientDBDao(new ODatabaseDocumentTx("memory:test").<ODatabaseDocumentTx>create()));
//    }
//
//    @Override
//    public KeyedWrapper toBaseObject(TransientObject object) {
//        return new KeyedWrapper(object);
//    }
//
//    @Override
//    public void rawSave(List<KeyedWrapper> keyedWrappers) {
//        for(KeyedWrapper kw: keyedWrappers){
//            kw.save();
//        }
//    }
//
//    @Override
//    public void setUp() {
//
//    }
//
//    @Override
//    public void tearDown() {
//
//    }
//
//    public static class KeyedWrapper extends Wrapper implements Keyable{
//
//        public KeyedWrapper(){
//            super();
//        }
//
//        public KeyedWrapper(TransientObject t){
//            super(t);
//        }
//
//        @Override
//        public String getKey() {
//            return super.getKey();
//        }
//    };
//}
