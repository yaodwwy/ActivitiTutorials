package cn.adbyte.activiti.$5_开始事件_结束事件_边界事件.delegate;

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
