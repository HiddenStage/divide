/*
 * Copyright (C) 2014 Divide.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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