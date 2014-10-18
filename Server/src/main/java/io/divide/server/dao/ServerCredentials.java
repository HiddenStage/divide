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

package io.divide.server.dao;

import io.divide.shared.util.ReflectionUtils;
import io.divide.shared.transitory.Credentials;
import io.divide.shared.transitory.TransientObject;

import java.util.Map;

public class ServerCredentials extends Credentials {

    public ServerCredentials(TransientObject serverObject){
        try {
            Map meta = (Map) ReflectionUtils.getObjectField(serverObject, TransientObject.META_DATA);
            Map user = (Map) ReflectionUtils.getObjectField(serverObject,TransientObject.USER_DATA);

            ReflectionUtils.setObjectField(this, TransientObject.META_DATA, meta);
            ReflectionUtils.setObjectField(this, TransientObject.USER_DATA, user);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setOwnerId(Integer id){
        super.setOwnerId(id);
    }

}
