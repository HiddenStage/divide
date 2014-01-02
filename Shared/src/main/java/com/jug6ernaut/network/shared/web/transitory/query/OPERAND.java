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
    EQ("==");

    String symbol;

    OPERAND(String symbol){
        this.symbol = symbol;
    }

    public static OPERAND from(String symbol){
        if(GREATER_THAN.symbol.equals(symbol)){
            return GREATER_THAN;
        } else if(LESS_THAN.symbol.equals(symbol)){
            return LESS_THAN;
        } else if(GREATER_THAN_EQ.symbol.equals(symbol)){
            return GREATER_THAN_EQ;
        } else if(LESS_THAN_EQ.symbol.equals(symbol)){
            return LESS_THAN_EQ;
        } else if(EQ.symbol.equals(symbol)){
            return EQ;
        }else {
            return null;
        }
    }

    public static enum Conditional{
        AND("&&"),
        OR("||");

        String symbol;

        Conditional(String symbol){
            this.symbol = symbol;
        }

        public static Conditional from(String symbol){
            if(AND.symbol.equals(symbol)){
                return AND;
            } else if(OR.symbol.equals(symbol)){
                return OR;
            } else {
                return null;
            }
        }
    }

};