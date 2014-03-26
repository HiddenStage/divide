package io.divide.dao.appengine;

import io.divide.shared.web.transitory.TransientObject;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 8/5/13
 * Time: 4:56 PM
 */

public class BackendToOfy{

    private BackendToOfy(){}

    public static OfyObject getOfy(TransientObject transientObject) {
        OfyObject oo = new OfyObject(transientObject.getObjectKey(),transientObject.getUserData(),transientObject.getMetaData());
        return oo;
    }

    public static TransientObject getBack(OfyObject ofyObject) {
        TempObject beo = new TempObject(); // gonna get over written anyways
        beo.setMaps(ofyObject.user_data,ofyObject.meta_data);
        return beo;
    }

    private static class TempObject extends TransientObject{

        protected <T extends TransientObject> TempObject() {
            super(TempObject.class); // doesnt matter
        }

        public void setMaps(Map<String,Object> userData, Map<String,String> metaData){
            this.user_data = userData;
            this.meta_data = metaData;
        }
    }

}