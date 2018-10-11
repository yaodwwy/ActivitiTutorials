package cn.adbyte.activiti.$5_开始事件_结束事件_边界事件.delegate;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

/**
 * @author Adam
 */
public class EventDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution arg0) {
        System.out.println("正常处理事件委托");
    }

}
