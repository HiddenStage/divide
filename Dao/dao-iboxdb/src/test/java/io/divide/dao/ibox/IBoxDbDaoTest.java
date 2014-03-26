//package io.divide.dao.ibox;
//
//import com.jug6ernauio.divide
//import com.jug6ernaut.network.dao.Keyable;
//import io.divide.shared.web.transitory.TransientObject;
//import iBoxDB.LocalServer.BoxSystem;
//import iBoxDB.LocalServer.DB;
//
//import java.util.List;
//
///**
// * Created by williamwebb on 2/15/14.
// */
//public class IBoxDbDaoTest extends DAOTest<IBoxDbDaoTest.KeyedWrapper>{
//
//    public static IBoxDbDao dao = new IBoxDbDao(new DB());
//
//    public IBoxDbDaoTest() {
//        super(dao);
//    }
//
//    @Override
//    public KeyedWrapper toBaseObject(TransientObject object) {
//        return new KeyedWrapper(object);
//    }
//
//    @Override
//    public void rawSave(List<KeyedWrapper> wrappers) {
//        for(KeyedWrapper k : wrappers) dao.box.insert("Wrapper",k);
//    }
//
//    @Override
//    public void setUp() {
//        BoxSystem.DBDebug.DeleteDBFiles();
//    }
//
//    @Override
//    public void tearDown() {
//        BoxSystem.DBDebug.DeleteDBFiles();
//    }
//
//    public static class KeyedWrapper extends Wrapper implements Keyable{
//
//        public KeyedWrapper(TransientObject o){
//            super(o);
//        }
//
//        @Override
//        public String getKey() {
//            return super.Key();
//        }
//    }
//}
