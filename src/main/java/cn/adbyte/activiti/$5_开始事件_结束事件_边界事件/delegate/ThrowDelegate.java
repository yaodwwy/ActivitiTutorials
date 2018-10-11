package cn.adbyte.activiti.$5_开始事件_结束事件_边界事件.delegate;

import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

public class ThrowDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution arg0) {
        System.out.println("主流程即将抛出异常 触发Event sub Process");
        throw new BpmnError("errorCode 400");
    }

}
