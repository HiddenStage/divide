package io.divide.client;

import io.divide.shared.web.transitory.Credentials;
import io.divide.shared.web.transitory.FilePermissions;
import io.divide.shared.web.transitory.TransientObject;

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
    private static final MetaKey OJBECT_TYPE_KEY = TransientObject.OBJECT_TYPE_KEY;
    private static final MetaKey USER_ID_KEY = TransientObject.OWNER_ID_KEY;

    public BackendObject() {
        this(BackendObject.class);
    }

    protected <B extends BackendObject> BackendObject(Class<B> type) {
        super(type);
//        this.setUserId(Backend.get().getAuthManager().getUser().getUserId());
        FilePermissions fp = new FilePermissions();
        fp.setWritable(true, FilePermissions.Level.OWNER, FilePermissions.Level.GROUP, FilePermissions.Level.WORLD);
        setFilePermissions(fp);
    }

    @Override
    protected final Credentials getLoggedInUser(){
        return BackendUser.getUser();
    }

    @Override
    public void setOwnerId(Integer userId){
        super.setOwnerId(userId);
    }

}
