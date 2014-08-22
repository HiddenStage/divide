/*
 * Copyright (C) 2014 Divide.io
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
