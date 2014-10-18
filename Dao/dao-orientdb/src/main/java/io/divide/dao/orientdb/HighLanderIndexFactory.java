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

import com.orientechnologies.orient.core.db.record.ODatabaseRecord;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.exception.OConfigurationException;
import com.orientechnologies.orient.core.index.ODefaultIndexFactory;
import com.orientechnologies.orient.core.index.OIndexFactory;
import com.orientechnologies.orient.core.index.OIndexInternal;
import com.orientechnologies.orient.core.index.engine.OMVRBTreeIndexEngine;

import java.util.Collections;
import java.util.Set;

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