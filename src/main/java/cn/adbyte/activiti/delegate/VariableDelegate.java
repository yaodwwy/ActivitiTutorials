package cn.adbyte.activiti.delegate;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

import java.util.Map;

public class VariableDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) {
        System.out.println("=============变量输出类=============");
        Map<String, Object> variables = execution.getVariables();
        System.out.println(variables);
    }
}
