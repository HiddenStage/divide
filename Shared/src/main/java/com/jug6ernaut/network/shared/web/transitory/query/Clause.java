package com.jug6ernaut.network.shared.web.transitory.query;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 8/22/13
 * Time: 9:43 PM
 */
public class Clause {
    private String before;
    private String operand;
    private String after;

    public Clause(){}

    public Clause(String before,OPERAND operand, String after){
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

    @Override
    public String toString() {
        return "Clause{" +
                "before='" + before + '\'' +
                ", operand='" + operand + '\'' +
                ", after='" + after + '\'' +
                '}';
    }
}
