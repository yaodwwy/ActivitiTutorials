package cn.adbyte.activiti.listenner;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

public class TaskCompleteListener implements TaskListener {

    @Override
    public void notify(DelegateTask arg0) {
        System.out.println("完成任务的时候触发的");
    }

}
