package io.divide.dao.orientdb;

import com.orientechnologies.orient.core.db.record.ODatabaseRecord;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.exception.OConfigurationException;
import com.orientechnologies.orient.core.index.ODefaultIndexFactory;
import com.orientechnologies.orient.core.index.OIndexFactory;
import com.orientechnologies.orient.core.index.OIndexInternal;
import com.orientechnologies.orient.core.index.engine.OMVRBTreeIndexEngine;

import java.util.Collections;
import java.util.Set;

/**
 * Created by williamwebb on 2/23/14.
 */
public class HighLanderIndexFactory implements OIndexFactory {

    @Override
    public Set<String> getTypes() {
        return Collections.singleton(OIndexHighLander.ID);
    }

    @Override
    public OIndexInternal<?> createIndex(ODatabaseRecord database, String indexType, String algorithm,
                                         String valueContainerAlgorithm) throws OConfigurationException {
        return new OIndexHighLander(indexType, ODefaultIndexFactory.SBTREE_ALGORITHM, new OMVRBTreeIndexEngine<OIdentifiable>(), valueContainerAlgorithm);
    }
}