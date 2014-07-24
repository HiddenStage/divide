package io.divide.shared.transitory.query;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 8/22/13
 * Time: 9:43 PM
 */
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
