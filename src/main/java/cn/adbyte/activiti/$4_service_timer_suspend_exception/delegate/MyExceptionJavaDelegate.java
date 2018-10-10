package cn.adbyte.activiti.$4_service_timer_suspend_exception.delegate;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

public class MyExceptionJavaDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) {
        System.out.println("====>> 异常处理类");
        throw new RuntimeException("异常处理类 抛出");
    }
}
