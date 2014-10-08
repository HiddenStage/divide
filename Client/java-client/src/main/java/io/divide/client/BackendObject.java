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

package io.divide.client;

import io.divide.shared.transitory.Credentials;
import io.divide.shared.transitory.FilePermissions;
import io.divide.shared.transitory.TransientObject;

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
        fp.setWritable(true, FilePermissions.Level.GROUP, FilePermissions.Level.WORLD);
        setFilePermissions(fp);
    }

    @Override
    protected final Credentials getLoggedInUser(){
        return BackendUser.getUser();
    }

    /**
     * Sets the user id for this object. Once set this can not be changed.
     * @param userId user id to be set.
     */
    @Override
    public void setOwnerId(Integer userId){
        super.setOwnerId(userId);
    }

}
