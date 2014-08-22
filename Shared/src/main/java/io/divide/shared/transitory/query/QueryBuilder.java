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

    public SelectBuilder select(SelectOperation... select){
        return new SelectBuilder(this,select);
    }

    public DeleteBuilder delete(){
        return new DeleteBuilder(this);
    }

    public UpdateBuilder update(){
        return new UpdateBuilder(this);
    }

    private void addWhere(String one, OPERAND operand, String two){
        query.where.put(query.where.size(),new UserDataClause(one,operand,two));
    }

    private void addWhere(TransientObject.MetaKey one, OPERAND operand, String two){
        query.where.put(query.where.size(),new MetaDataClause(one.KEY,operand,two));
    }

    private void addWhere(OPERAND.Conditional conditional,String one, OPERAND operand, String two){
        query.where.put(query.where.size(),new UserDataClause(conditional,one,operand,two));
    }

    private void addWhere(OPERAND.Conditional conditional,TransientObject.MetaKey one, OPERAND operand, String two){
        query.where.put(query.where.size(),new MetaDataClause(conditional,one.KEY,operand,two));
    }

    private void setFrom(Class from){
        query.from = Query.safeTable(from);
    }

    private void addSelect(SelectOperation select){
        if(select!=null)
            query.select = select;
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

    private void setRandom(boolean random){ query.random = random; }

    private Query getQuery(){
        return query;
    }

    public class SelectBuilder extends QueryActionBuilder{

        private SelectBuilder(QueryBuilder builder, SelectOperation... select){
            super(builder,QueryAction.SELECT);
            if (select!=null)
                for (SelectOperation s : select)
                    builder.addSelect(s);
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

        public <T extends TransientObject> WhereBuilder from(Class<T> from){
            builder.setFrom(from);
            return new WhereBuilder(builder);
        }
    }

    public class WhereBuilder{
        private QueryBuilder builder;

        protected WhereBuilder(QueryBuilder builder){
            this.builder = builder;
        }

        public WhereMoreBuilder where(String one, OPERAND operand, String two){
            builder.addWhere(one,operand,two);
            return new WhereMoreBuilder(builder);
        }

        public WhereMoreBuilder where(TransientObject.MetaKey one, OPERAND operand, String two){
            builder.addWhere(one,operand,two);
            return new WhereMoreBuilder(builder);
        }

        public LimitConstraintBuilder limit(Integer limit){
            return new LimitConstraintBuilder(builder,limit);
        }

        public OffsetConstraintBuilder offset(Integer offset){
            return new OffsetConstraintBuilder(builder,offset);
        }

        public RandomConstraintBuilder random( Integer limit){
            return new RandomConstraintBuilder(builder,limit);
        }

        public Query build(){
            return builder.getQuery();
        }
    }

    public class WhereMoreBuilder{
        private QueryBuilder builder;

        protected WhereMoreBuilder(QueryBuilder builder){
            this.builder = builder;
        }

        public WhereMoreBuilder and(String one, OPERAND operand, String two){
            builder.addWhere(OPERAND.Conditional.AND,one,operand,two);
            return this;
        }

        public WhereMoreBuilder or(String one, OPERAND operand, String two){
            builder.addWhere(OPERAND.Conditional.OR,one,operand,two);
            return this;
        }

        public WhereMoreBuilder and(TransientObject.MetaKey one, OPERAND operand, String two){
            builder.addWhere(OPERAND.Conditional.AND,one,operand,two);
            return this;
        }

        public WhereMoreBuilder or(TransientObject.MetaKey one, OPERAND operand, String two){
            builder.addWhere(OPERAND.Conditional.OR,one,operand,two);
            return this;
        }


        public LimitConstraintBuilder limit(Integer limit){
            return new LimitConstraintBuilder(builder,limit);
        }

        public OffsetConstraintBuilder offset(Integer offset){
            return new OffsetConstraintBuilder(builder,offset);
        }

        public RandomConstraintBuilder random(Integer limit){
            return new RandomConstraintBuilder(builder,limit);
        }

        public Query build(){
            return builder.getQuery();
        }    }

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

    public class RandomConstraintBuilder extends ConstraintBuilder{

        private RandomConstraintBuilder(QueryBuilder builder, Integer limit) {
            super(builder);
            builder.setRandom(true);
            builder.setLimit(limit);
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
