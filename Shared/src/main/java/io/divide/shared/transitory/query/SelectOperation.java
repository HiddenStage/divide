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

package io.divide.shared.transitory.query;

import io.divide.shared.transitory.TransientObject;

public enum SelectOperation {
    COUNT(Count.class);

    private transient Class<?> type;

    private <T extends TransientObject> SelectOperation(Class<T> type){
        this.type = type;
    }

    public Class<?> getType(){
        return type;
    }

    public String getErrorMessage(){
        return this.name() + " requires type " + type.getSimpleName();
    }


}
