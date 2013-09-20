package com.jug6ernaut.network.authenticator.server.endpoints;

import com.jug6ernaut.network.authenticator.server.dao.DAO;
import com.jug6ernaut.network.authenticator.server.dao.Session;
import com.jug6ernaut.network.authenticator.server.utils.ResponseUtils;
import com.jug6ernaut.network.shared.util.ObjectUtils;
import com.jug6ernaut.network.shared.web.transitory.TransientObject;
import com.jug6ernaut.network.shared.web.transitory.query.Query;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 8/19/13
 * Time: 6:10 PM
 */
@Path("/data")
public class DataEndpoint {
    private Logger logger = Logger.getLogger(DataEndpoint.class.getName());

    @Context
    DAO dao;

    @POST
    @Path("/get")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(Collection<TransientObject> keys) {
        try {
            return Response
                    .ok()
                    .entity(dao.get(ObjectUtils.c2v(keys)))
                    .build();
        }catch (DAO.DAOException e) {
            return ResponseUtils.fromDAOExpection(e);
        }
    }

    @POST
    @Path("/query")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response query(@Context Session session,Query query) {
        try {
            return Response
                    .ok()
                    .entity(dao.query(query))
                    .build();
        }catch (DAO.DAOException e) {
            return ResponseUtils.fromDAOExpection(e);
        }
    }

    @POST
    @Path("/save")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response save(Collection<TransientObject> objects) {
        try {
            dao.save(ObjectUtils.c2v(objects));

            return Response
                    .ok()
                    .build();
        }catch (DAO.DAOException e) {
            return ResponseUtils.fromDAOExpection(e);
        }
    }
//
//    @GET
//    @Path("/test/get/{one}/{two}/{three}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public List<TransientObject> getObjectTest(
//            @PathParam("one") String one,
//            @PathParam("two") String two,
//            @PathParam("three") String three,
//            @QueryParam("limit") Integer limit,
//            @QueryParam("offset") Integer offset,
//            @QueryParam("action") String action,
//            @QueryParam("from") String from) throws Exception {
//
//        Query q = null;
//
//        if (action!=null){
//            if(QueryAction.SELECT.is(action)){
//                q = new QueryBuilder().
//                        select(null).
//                        from(from).
//                        where(one, OPERAND.from(two), three).
//                        limit(limit).
//                        offset(offset).build();
//            }else if(QueryAction.DELETE.is(action)){
//                q = new QueryBuilder().
//                        delete().
//                        from(from).
//                        where(one, OPERAND.from(two), three).
//                        limit(limit).
//                        offset(offset).build();
//            }else if(QueryAction.UPDATE.is(action)){
//
//            }
//        } else {
//            q = new QueryBuilder().
//                    select(null).
//                    from(from).
//                    where(one, OPERAND.from(two), three).
//                    limit(limit).
//                    offset(offset).build();
//        }
//
//        List<TransientObject> objects = dao.query(q);
//
//        return objects;
//    }
//
//    @GET
//    @Path("/test/save/{one}/{two}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public TransientObject setObjectTest(@PathParam("one") String one,
//                                       @PathParam("two") String two,
//                                       @QueryParam("type") String type) throws Exception {
//
//        TransientObject b = new ServerObject(type);
//        b.add(one, two);
//
//
//        dao.save(b);
//
//        return b;
//    }

}
