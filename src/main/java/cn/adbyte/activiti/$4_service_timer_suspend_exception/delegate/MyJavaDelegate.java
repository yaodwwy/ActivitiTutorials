package cn.adbyte.activiti.$4_service_timer_suspend_exception.delegate;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

/**
 * @author Adam
 */
public class MyJavaDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        System.out.println("这是一个处理类");
    }
}
