package cn.adbyte.activiti.delegate._6中间抛出事件补偿;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

public class IcbcAddDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution arg0) {
        System.out.println("工商银行加款");
    }

}
