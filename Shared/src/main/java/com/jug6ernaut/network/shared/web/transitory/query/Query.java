package com.jug6ernaut.network.shared.web.transitory.query;

import com.jug6ernaut.network.shared.web.transitory.TransientObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 8/19/13
 * Time: 6:43 PM
 */
public class Query<T extends TransientObject> {

    protected Query(){

    }

    protected QueryBuilder.QueryAction action;
    protected String from;
    protected final Map<Integer,Clause> where = new HashMap<Integer,Clause>();
    protected SelectOperation select = null;
    protected Integer limit;
    protected Integer offset;
    protected Boolean random;

    public QueryBuilder.QueryAction getAction() {
        return action;
    }

    public String getFrom() {
        return from;
    }

    public Map<Integer, Clause> getWhere() {
        return where;
    }

    public SelectOperation getSelect() {
        return select;
    }

    public Integer getLimit() {
        return limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public Boolean getRandom() { return random; }

    public String getSQL(){

        String sql = "";
        switch (action){
            case SELECT:{
                if(select==null){
                    sql = "SELECT * FROM " + from;

                    if(!where.isEmpty()){

                        sql += " WHERE";

                        Collection<Clause> wheres = where.values();
                        for(Clause e : wheres){
                            sql += " " + e.getCoded() + " AND";
                        }
                        sql = sql.substring(0,sql.length()-3);
                    }

                    if(limit != null){
                        sql += " LIMIT " + limit;
                    }
                } else {
                    switch (select){
                        case COUNT:{
                            sql = "SELECT count(*) from " + from;

                            if(!where.isEmpty()){
                                Collection<Clause> wheres = where.values();
                                for(Clause e : wheres){
                                    sql += " " + e.getCoded() + " AND";
                                }
                                sql = sql.substring(0,sql.length()-3);
                            }
                        }break;
                    }
                }
            }break;
            case DELETE:{

            }break;
            case UPDATE:{

            }break;
        }

        return sql;
    }

    @Override
    public String toString() {
        return "Query{" +
                "action=" + action +
                ", from='" + from + '\'' +
                ", where=" + where +
                ", select=" + select +
                ", limit=" + limit +
                ", offset=" + offset +
                '}';
    }
}
