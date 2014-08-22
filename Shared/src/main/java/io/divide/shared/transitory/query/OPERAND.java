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

public enum OPERAND{

    GREATER_THAN(">"),
    LESS_THAN("<"),
    GREATER_THAN_EQ(">="),
    LESS_THAN_EQ("<="),
    EQ("=="),
    CONTAINS("CONTAINS");

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
        } else if(CONTAINS.symbol.equals(symbol)){
            return CONTAINS;
        }else {
            return null;
        }
    }

    public static enum Conditional{
        AND("AND"),
        OR("OR");

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

    @Override
    public String toString(){
        return symbol;
    }

};