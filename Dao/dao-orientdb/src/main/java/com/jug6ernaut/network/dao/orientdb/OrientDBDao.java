package com.jug6ernaut.network.dao.orientdb;

import com.jug6ernaut.network.dao.DAO;
import com.jug6ernaut.network.shared.web.transitory.TransientObject;
import com.jug6ernaut.network.shared.web.transitory.query.Query;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.core.tx.OTransaction;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by williamwebb on 2/14/14.
 */
public class OrientDBDao implements DAO{

    ODatabaseDocumentTx db;

    public OrientDBDao(ODatabaseDocumentTx db){
        this.db = db;
    }

    @Override
    public List<TransientObject> query(Query query) throws DAO.DAOException {
        List<Wrapper> objects = db.query(new OSQLSynchQuery<Wrapper>(query.getSQL()));
        List list = new ArrayList();
        for(Wrapper w : objects){
            list.add(w.toObject(TransientObject.class));
        }
        return list;
    }

    @Override
    public Collection<TransientObject> get(String... keys) throws DAOException {
//        List list = db.query(new OSQLSynchQuery<ODocument>("SELECT FROM Wrapper WHERE meta_data.object_type = "  + objectType));

        return null;
    }

    @Override
    public void save(TransientObject... objects) throws DAOException {
        OTransaction transaction = db.getTransaction();
        transaction.begin();
        for(TransientObject t : objects){
            new Wrapper(t).save();
        }
        transaction.commit();
        transaction.close();
    }

    @Override
    public void delete(TransientObject... objects) throws DAOException {
        for(TransientObject o : objects){
            db.delete(new ORecordId(o.getObjectKey()));
        }
    }

    @Override
    public boolean exists(TransientObject... objects) {
        boolean found = true;
        for(TransientObject o : objects){
            if( db.getRecord(new ORecordId(o.getObjectKey())) == null ) found = false;
        }
        return found;
    }

    @Override
    public int count(String objectType) {
        return ((ODocument)db.query(new OSQLSynchQuery<ODocument>("SELECT COUNT(*) as count FROM Wrapper WHERE meta_data.object_type = "  + objectType)).get(0)).field("count");
    }

    @Override
    public KeyPair keys(KeyPair keys) {
        return null;
    }
}
