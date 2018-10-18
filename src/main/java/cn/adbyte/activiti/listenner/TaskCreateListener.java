package cn.adbyte.activiti.listenner;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.el.FixedValue;

public class TaskCreateListener implements TaskListener {

    private FixedValue userName;
 
    public void setUserName(FixedValue userName) {
        this.userName = userName;
    }


    @Override
    public void notify(DelegateTask arg0) {
        System.out.println("这是自定义任务监听器, " + userName.getExpressionText());
    }

}
