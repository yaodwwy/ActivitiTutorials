package cn.adbyte.activiti.listenner;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

public class TaskAssignmentListener implements TaskListener {

    @Override
    public void notify(DelegateTask arg0) {
        System.out.println("指定代理人时触发的");
    }

}
