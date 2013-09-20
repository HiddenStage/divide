package com.jug6ernaut.network.shared.web.transitory.query;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 8/19/13
 * Time: 7:14 PM
 */
public abstract class AbstractClause {

    public abstract String type();

    public Clause eq(String where){
        return new Clause(type(),OPERAND.EQ,where);
    };
    public Clause less(String where){
        return new Clause(type(),OPERAND.LESS_THAN,where);
    };
    public Clause greater(String where){
        return new Clause(type(),OPERAND.GREATER_THAN,where);
    };
    public Clause less_eq(String where){
        return new Clause(type(),OPERAND.LESS_THAN_EQ,where);
    };
    public Clause greater_eq(String where){
        return new Clause(type(),OPERAND.GREATER_THAN_EQ,where);
    };

}
