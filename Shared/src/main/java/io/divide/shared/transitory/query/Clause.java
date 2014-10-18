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
