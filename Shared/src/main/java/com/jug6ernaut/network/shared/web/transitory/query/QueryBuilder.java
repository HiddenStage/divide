package com.jug6ernaut.network.shared.web.transitory.query;

import com.jug6ernaut.network.shared.web.transitory.TransientObject;

import java.util.Collections;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 8/19/13
 * Time: 7:13 PM
 */
public class QueryBuilder{

    public static enum QueryAction{
        SELECT,
        DELETE,
        UPDATE;

        public boolean is(String str){
            return this.name().equals(str.toUpperCase());
        }
    }

    private Query query;

    public QueryBuilder(){
        query = new Query();
    }

    public SelectBuilder select(String... select){
        return new SelectBuilder(this,select);
    }

    public DeleteBuilder delete(){
        return new DeleteBuilder(this);
    }

    public UpdateBuilder update(){
        return new UpdateBuilder(this);
    }

    private void addWhere(String one, OPERAND operand, String two){
        query.where.put(query.where.size(),new Clause(TransientObject.getKey(one),operand,two));
    }

    private void setFrom(String from){
        query.from = from;
    }

    private void addSelect(String... select){
        if(select!=null)
            Collections.addAll(query.select, select);
    }

    private void setLimit(Integer limit){
        query.limit = limit;
    }

    private void setOffset(Integer offset){
        query.offset = offset;
    }

    private void setAction(QueryAction action){
        query.action = action;
    }

    private Query getQuery(){
        return query;
    }

    public class SelectBuilder extends QueryActionBuilder{

        private SelectBuilder(QueryBuilder builder, String... select){
            super(builder,QueryAction.SELECT);
            builder.addSelect(select);
        }

    }

    public class DeleteBuilder extends QueryActionBuilder{

        private DeleteBuilder(QueryBuilder builder) {
            super(builder,QueryAction.DELETE);
        }

    }

    public class UpdateBuilder extends QueryActionBuilder{

        private UpdateBuilder(QueryBuilder builder) {
            super(builder,QueryAction.UPDATE);
        }

    }

    public class QueryActionBuilder{
        private QueryBuilder builder;

        private QueryActionBuilder(QueryBuilder builder, QueryAction action){
            this.builder = builder;
            this.builder.setAction(action);
        }

        public WhereBuilder from(String from){
            builder.setFrom((from==null?"":from));
            return new WhereBuilder(builder);
        }

        public Query build(){
            return builder.getQuery();
        }

    }

    public class WhereBuilder{
        private QueryBuilder builder;

        private WhereBuilder(QueryBuilder builder){
            this.builder = builder;
        }

        public WhereMoreBuilder where(String one, OPERAND operand, String two){
            builder.addWhere(one,operand,two);
            return new WhereMoreBuilder(builder);
        }

        public LimitConstraintBuilder limit(Integer limit){
            return new LimitConstraintBuilder(builder,limit);
        }

        public OffsetConstraintBuilder offset(Integer offset){
            return new OffsetConstraintBuilder(builder,offset);
        }

    }

    public class WhereMoreBuilder{
        private QueryBuilder builder;

        private WhereMoreBuilder(QueryBuilder builder){
            this.builder = builder;
        }

        public WhereMoreBuilder and(String one, OPERAND operand, String two){
            builder.addWhere(one,operand,two);
            return this;
        }

        public WhereMoreBuilder or(String one, OPERAND operand, String two){
            builder.addWhere(one,operand,two);
            return this;
        }

        public LimitConstraintBuilder limit(Integer limit){
            return new LimitConstraintBuilder(builder,limit);
        }

        public OffsetConstraintBuilder offset(Integer offset){
            return new OffsetConstraintBuilder(builder,offset);
        }

        public Query build(){
            return builder.getQuery();
        }
    }

    public class LimitConstraintBuilder extends ConstraintBuilder{

        private LimitConstraintBuilder(QueryBuilder builder, Integer limit){
            super(builder);
            this.builder.setLimit(limit);
        }

        public ConstraintBuilder offset(Integer offset){
            if(bothSet()){
                return new ConstraintBuilder(builder);
            }else {
                return new OffsetConstraintBuilder(builder,offset);
            }
        }

    }

    public class OffsetConstraintBuilder extends ConstraintBuilder{

        private OffsetConstraintBuilder(QueryBuilder builder, Integer offset) {
            super(builder);
            builder.setOffset(offset);
        }

        public ConstraintBuilder limit(Integer limit){
            if(bothSet()){
                return new ConstraintBuilder(builder);
            }else {
                return new LimitConstraintBuilder(builder,limit);
            }
        }
    }

    public class ConstraintBuilder{
        protected QueryBuilder builder;

        private ConstraintBuilder(QueryBuilder builder){
            this.builder = builder;
        }

        public Query build(){
            return builder.getQuery();
        }

        protected boolean bothSet(){
            return (builder.query.limit != null && builder.query.offset != null);
        }
    }
}
