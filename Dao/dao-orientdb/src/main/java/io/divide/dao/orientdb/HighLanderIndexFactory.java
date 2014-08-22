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