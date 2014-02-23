package com.jug6ernaut.network.dao.orientdb;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.index.OIndexDictionary;
import com.orientechnologies.orient.core.index.OIndexEngine;
import com.orientechnologies.orient.core.index.OIndexOneValue;

/**
 * Created by williamwebb on 2/23/14.
 */
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
