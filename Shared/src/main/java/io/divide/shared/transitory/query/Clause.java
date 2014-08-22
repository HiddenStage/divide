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

public class Clause {
    private String preOperator = "";
    private String before;
    private String operand;
    private String after;

    private Clause(){}

    protected Clause(String before,OPERAND operand, String after){
        this.before = before;
        this.after = after;
        this.operand = operand.symbol;
    }

    protected Clause(OPERAND.Conditional preOperator, String before,OPERAND operand, String after){
        this.preOperator = preOperator.symbol;
        this.before = before;
        this.after = after;
        this.operand = operand.symbol;
    }

    public String getBefore(){
        return before;
    }

    public String getOperand(){
        return operand;
    }

    public String getAfter(){
        return after;
    }

    public void setBefore(String before){
        this.before = before;
    }

    public void setOperand(OPERAND operand){
        this.operand = operand.symbol;
    }

    public String getPreOperator(){
        return preOperator;
    }

    public void setAfter(String after){
        this.after = after;
    }

    public String getCoded(){
        return getBefore() + " " + getOperand() + " '" + getAfter() +"'";
    }

    @Override
    public String toString() {
        return "Clause{" +
                "before='" + before + '\'' +
                ", operand='" + operand + '\'' +
                ", after='" + after + '\'' +
                '}';
    }
}
