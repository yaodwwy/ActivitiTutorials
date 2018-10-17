package cn.adbyte.activiti.delegate._6中间抛出事件补偿;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

public class MyExecutionListener implements ExecutionListener {
    @Override
    public void notify(DelegateExecution execution) {
        System.out.println("抛出补偿信号：" + execution.getEventName());
    }
}
