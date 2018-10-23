package cn.adbyte.activiti.delegate;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.VariableScope;

public class BusinessRuleTaskDelegate implements org.activiti.engine.delegate.BusinessRuleTaskDelegate {
    @Override
    public void addRuleVariableInputIdExpression(Expression inputId) {
        System.out.println(inputId.getExpressionText());
        System.out.println("addRuleVariableInputIdExpression");
    }

    @Override
    public void addRuleIdExpression(Expression inputId) {
        System.out.println("addRuleIdExpression");

    }

    @Override
    public void setExclude(boolean exclude) {
        System.out.println("exclude");

    }

    @Override
    public void setResultVariable(String resultVariableName) {

        System.out.println("resultVariableName");
    }

    @Override
    public void execute(DelegateExecution execution) {

        System.out.println("execution");
    }
}
