package cn.adbyte.activiti.$5_开始事件_结束事件_边界事件;

import cn.adbyte.activiti.ActivitiConfig;
import cn.adbyte.activiti.Print;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author Adam
 */
@RunWith(SpringRunner.class)
@Import({ActivitiConfig.class})
public class 中间捕获事件测试 {

    @Autowired
    private RepositoryService rs;

    @Autowired
    private RuntimeService runService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ManagementService managementService;


    @Test
    public void 中间定时器捕获事件() throws InterruptedException {
        Deployment dep = rs.createDeployment().addClasspathResource("CatchTimerEvent.bpmn20.xml").deploy();
        ProcessDefinition pd = rs.createProcessDefinitionQuery().deploymentId(dep.getId()).singleResult();
        // 启动流程
        ProcessInstance pi = runService.startProcessInstanceById(pd.getId());

        Task task = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
        System.out.println("当前任务：" + task.getName());
        System.out.println("等10秒后会自动超时...");
        Thread.sleep(1000 * 10);

        task = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
        System.out.println("当前任务：" + task.getName());
    }

    @Test
    public void 中间信号捕获事件() {
        Deployment dep = rs.createDeployment().addClasspathResource("CatchSignalEvent.bpmn20.xml").deploy();
        ProcessDefinition pd = rs.createProcessDefinitionQuery().deploymentId(dep.getId()).singleResult();
        // 启动流程
        ProcessInstance pi = runService.startProcessInstanceById(pd.getId());

        System.out.println(pi.getId());

        Task task = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
        System.out.println("当前任务：" + task.getName());
        // 完成第一个任务
        taskService.complete(task.getId());

        task = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
        System.out.println("当前任务：" + task.getName());

        // 信号触发
        runService.signalEventReceived("contactChangeSignal");

        task = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
        System.out.println("当前任务：" + task.getName());
    }

}
