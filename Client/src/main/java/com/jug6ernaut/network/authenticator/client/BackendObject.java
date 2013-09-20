package com.jug6ernaut.network.authenticator.client;

import com.jug6ernaut.network.shared.web.transitory.TransientObject;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 8/25/13
 * Time: 7:43 PM
 */
public class BackendObject extends TransientObject {
    // FIXME stupidness of all this is just to have the
    // "this.setUserId()" in the constructor, all this nastyness is to hide
    // implementation info from TransientObject. Possible fix, have two implementations of same
    // class, data struct just needs to match, not implementation. Lose type safety...Is it really needed?
    private static final String USER_DATA = TransientObject.USER_DATA;
//    private static final String META_DATA = TransientObject.META_DATA;
    private static final String OJBECT_TYPE_KEY = TransientObject.OJBECT_TYPE_KEY;
    private static final String USER_ID_KEY = TransientObject.USER_ID_KEY;
    BackendObject(String objectType) {
        super(objectType);
        //this.setUserId(Backend.get().getAuthManager().getUser().getUserId());
    }

    public static BackendObject createNoType(){
        BackendObject b = new BackendObject("");
        return b;
    }

    public static BackendObject create(String objectType){
        BackendObject b = new BackendObject(objectType);
        return b;
    }

    protected void setUserId(String userId){
        this.setUserId(userId);
    }

}
