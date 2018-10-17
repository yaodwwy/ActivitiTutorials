package cn.adbyte.activiti.delegate;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

/**
 * @author Adam
 */
public class SubEventDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution arg0) {
        System.out.println("主流程发生了异常，现在是事件子流程的内容");
    }

}
