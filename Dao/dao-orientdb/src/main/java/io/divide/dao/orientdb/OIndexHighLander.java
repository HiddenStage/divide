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

package io.divide.dao.orientdb;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.index.OIndexDictionary;
import com.orientechnologies.orient.core.index.OIndexEngine;
import com.orientechnologies.orient.core.index.OIndexOneValue;

public class OIndexHighLander extends OIndexDictionary {

    public static String ID = "HIGHLANDER_INDEX";

    public OIndexHighLander(String typeId, String algorithm, OIndexEngine<OIdentifiable> engine, String valueContainerAlgorithm) {
        super(typeId, algorithm, engine, valueContainerAlgorithm);
    }

    public OIndexOneValue put(final Object iKey, final OIdentifiable iSingleValue) {
        acquireExclusiveLock();
        try {
            checkForKeyType(iKey);

            final OIdentifiable value = super.get(iKey);

            if (value != null){
                // DELETE THE PREVIOUS INDEXED RECORD
                value.getRecord().delete();
            }
            super.put(iKey, iSingleValue);

            return this;

        } finally {
            releaseExclusiveLock();
        }
    }
}
