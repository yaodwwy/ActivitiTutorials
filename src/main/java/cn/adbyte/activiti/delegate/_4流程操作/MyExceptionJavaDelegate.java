package cn.adbyte.activiti.delegate._4流程操作;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

/**
 * @author Adam
 */
public class MyExceptionJavaDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) {
        System.out.println("====>> 异常处理类");
        throw new RuntimeException("异常处理类 抛出");
    }
}
