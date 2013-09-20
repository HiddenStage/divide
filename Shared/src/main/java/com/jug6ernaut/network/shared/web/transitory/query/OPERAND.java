package com.jug6ernaut.network.shared.web.transitory.query;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 8/22/13
 * Time: 9:38 PM
 */
public enum OPERAND{

    GREATER_THAN(">"),
    LESS_THAN("<"),
    GREATER_THAN_EQ(">="),
    LESS_THAN_EQ("<="),
    EQ("="),
    AND("&&"),
    OR("||");

    String symbol;

    OPERAND(String symbol){
        this.symbol = symbol;
    }

    public static OPERAND from(String symbol){
        if(symbol.equals(GREATER_THAN.symbol)){
            return GREATER_THAN;
        } else if(symbol.equals(LESS_THAN.symbol)){
            return LESS_THAN;
        } else if(symbol.equals(GREATER_THAN_EQ.symbol)){
            return GREATER_THAN_EQ;
        } else if(symbol.equals(LESS_THAN_EQ.symbol)){
            return LESS_THAN_EQ;
        } else if(symbol.equals(EQ.symbol)){
            return EQ;
        } else if(symbol.equals(AND.symbol)){
            return AND;
        } else if(symbol.equals(OR.symbol)){
            return OR;
        } else {
            return null;
        }
    }

};