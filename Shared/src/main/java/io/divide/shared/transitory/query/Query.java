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

package io.divide.shared.transitory.query;

import io.divide.shared.transitory.TransientObject;

import java.util.HashMap;
import java.util.Map;

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
        return safeTable(type.getName());
    }

    public static String safeTable(String type){
        return type.replace(".","_");
    }

    public static <T extends TransientObject> String reverseTable(Class<T> type){
        return reverseTable(type.getName());
    }

    public static String reverseTable(String type){
        return type.replace("_",".");
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
