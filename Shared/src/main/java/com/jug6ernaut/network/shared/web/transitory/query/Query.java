package com.jug6ernaut.network.shared.web.transitory.query;

import com.jug6ernaut.network.shared.web.transitory.TransientObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 8/19/13
 * Time: 6:43 PM
 */
public class Query {

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
                        if(!where.isEmpty()){
                            sql += buildWhere(where);
                        }
                    }

                    if(limit != null){
                        sql += " LIMIT " + limit;
                    }
                } else {
                    switch (select){
                        case COUNT:{
                            sql = "SELECT count(*) from " + from;
                            if(!where.isEmpty()){
                                sql += buildWhere(where);
                            }
                        }break;
                    }
                }
            }break;
            case DELETE:{
                    sql = "DELETE FROM " + from;

                    if(!where.isEmpty()){
                        sql += buildWhere(where);
                    }
                    if(limit != null){
                        sql += " LIMIT " + limit;
                    }
            }break;
            case UPDATE:{

            }break;
        }

        return sql;
    }

    private String buildWhere(Map<Integer,Clause> clauses){
        if(clauses.size()==0)return "";
        StringBuilder sb = new StringBuilder();
        sb.append(" WHERE ");
        sb.append(clauses.get(0).getCoded());
        for(int x=1;x<clauses.size();x++){
            Clause c = clauses.get(x);
            sb.append(" ");
            sb.append(c.getPreOperator());
            sb.append(" ");
            sb.append(c.getCoded());
        }
        return sb.toString();
    }

    public static <T extends TransientObject> String safeTable(Class<T> type){
        return type.getName().replace(".","_");
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
