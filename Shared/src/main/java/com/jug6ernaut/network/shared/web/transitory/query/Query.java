package com.jug6ernaut.network.shared.web.transitory.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    protected final List<String> select = new ArrayList<String>();
    protected Integer limit;
    protected Integer offset;

    public QueryBuilder.QueryAction getAction() {
        return action;
    }

    public String getFrom() {
        return from;
    }

    public Map<Integer, Clause> getWhere() {
        return where;
    }

    public List<String> getSelect() {
        return select;
    }

    public Integer getLimit() {
        return limit;
    }

    public Integer getOffset() {
        return offset;
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
