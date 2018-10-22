package cn.adbyte.activiti;

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

@RunWith(SpringRunner.class)
@Import({ActivitiConfig.class})
public class _6BPMN事件Test {

    @Autowired
    private ActivitiFactory ActivitiFactory;

    @Autowired
    private RuntimeService runService;

    @Autowired
    private TaskService taskService;
    
    @Autowired
    private RepositoryService repositoryService;

    @Test
    public void 定时器开始事件() throws InterruptedException {
        ActivitiFactory.deployAndStart("processes/_6BPMN定时器开始事件.bpmn20.xml");
        long dataCount = runService.createProcessInstanceQuery().count();
        System.out.println("5秒自动开启一个实例，sleep前流程实例数量：" + dataCount);
        for (int i = 0; i < 10; i++) {
            dataCount = runService.createProcessInstanceQuery().count();
            Thread.sleep(6000);
            System.out.println("sleep后流程实例数量：" + dataCount);
        }
    }

    @Test
    public void 消息开始事件() {
        repositoryService.createDeployment().addClasspathResource("processes/_6BPMN消息开始事件.bpmn20.xml").deploy();
        ProcessInstance pi = runService.startProcessInstanceByMessage("msgName");
        Print.instances(List.of(pi));
    }
    @Test
    public void 错误开始事件() {
        ActivitiFactory.deployAndStart("processes/_6BPMN错误开始事件.bpmn20.xml");
    }


    @Test
    public void 中间定时器捕获事件() throws Exception {
        ProcessInstance pi = ActivitiFactory.deployAndStart("processes/_6BPMN中间定时器捕获事件.bpmn20.xml");
        ActivitiFactory.complete(pi);
        Thread.sleep(10_000);
        ActivitiFactory.complete(pi);
    }

    @Test
    public void 中间信号捕获事件() {
        ProcessInstance pi = ActivitiFactory.deployAndStart("processes/_6BPMN中间信号捕获事件.bpmn20.xml");
        ActivitiFactory.complete(pi);
    }


    @Test
    public void 中间抛出事件补偿() {
        ActivitiFactory.deployAndStart("processes/_6BPMN中间抛出事件补偿.bpmn20.xml");
    }

    @Test
    public void 边界定时事件() throws InterruptedException {
        ProcessInstance processInstance = ActivitiFactory.deployAndStart("processes/_6BPMN边界定时事件.bpmn20.xml");
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        System.out.println("当前任务：" + task.getName());
        System.out.println("等10秒后会自动超时...");
        Thread.sleep(1000 * 10);
        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        System.out.println("当前任务：" + task.getName());
    }

    @Test
    public void 边界信号事件() {
        ProcessInstance processInstance = ActivitiFactory.deployAndStart("processes/_6BPMN边界信号事件.bpmn20.xml");
        
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        System.out.println("当前任务：" + task.getName());
        // 完成第一个任务
        taskService.complete(task.getId());

        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        System.out.println("当前任务：" + task.getName());

        // 信号触发
        System.out.println("信号触发->>contactChangeSignal");
        runService.signalEventReceived("contactChangeSignal");

        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        System.out.println("当前任务：" + task.getName());
    }

    @Test
    public void 错误结束事件() {
        ActivitiFactory.deployAndStart("processes/_6BPMN错误结束事件.bpmn20.xml");
        System.out.println("错误结束事件：结束时触发...");
    }

    @Test
    public void 取消结束事件() {
        ProcessInstance processInstance = ActivitiFactory.deployAndStart("processes/_6BPMN取消结束事件.bpmn20.xml");
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        System.out.println("当前流程任务：");
        Print.tasks(List.of(task));
        taskService.complete(task.getId());
        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        System.out.println("当前流程任务：");
        Print.tasks(List.of(task));
    }


    @Test
    public void 终止结束事件() {
        ProcessInstance processInstance = ActivitiFactory.deployAndStart("processes/_6BPMN终止结束事件.bpmn20.xml");
        List<Execution> list = runService.createExecutionQuery().processInstanceId(processInstance.getId()).list();
        System.out.println("终结前执行流：");
        Print.exec(list);

        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
        for (Task task : tasks) {
            if (task.getName().equals("Task1")) {
                taskService.complete(task.getId());
            }
        }

        list = runService.createExecutionQuery().processInstanceId(processInstance.getId()).list();
        System.out.println("终结后执行流：");

        Print.exec(list);

        ProcessInstance newPi = runService.createProcessInstanceQuery().processInstanceId(processInstance.getId()).singleResult();
        System.out.println("流程实例已终结，Task3不可达！");
    }
}
