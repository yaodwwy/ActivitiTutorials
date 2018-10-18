package cn.adbyte.activiti;

import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 子流程：
 * 嵌入式子流程 *  调用式子流程 *  事件子流程 *  事务子流程 *  特别子流程
 * <p>
 * 顺序流：
 * 条件顺序流 *  默认顺序流
 * <p>
 * 网关：
 * 单向网关 *  并行网关 *  兼容网关 *  事件网关 *  组合网关
 * <p>
 * 流程活动：
 * 多实例流程活动
 * 设置循环数据 跳出循环 补偿处理者
 */
@RunWith(SpringRunner.class)
@Import({ActivitiConfig.class})
public class _8BPMN其他元素Test {

    @Autowired
    private ActivitiFactory ActivitiFactory;

    @Autowired
    private RuntimeService runService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private TaskService taskService;

    @Test
    public void 嵌入式子流程() {
        ProcessInstance processInstance = ActivitiFactory.deployAndStart("processes/_8BPMN嵌入式子流程.bpmn20.xml");

        // 查询当前节点
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        System.out.println("当前流程任务：" + task.getName());
    }

    @Test
    public void 调用子流程() {
        ProcessInstance processInstance = ActivitiFactory.deployAndStart(
                "processes/_8BPMN调用子流程Sub.bpmn20.xml",
                "processes/_8BPMN调用子流程Main.bpmn20.xml"
        );

        // 查询当前节点
        List<Task> task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
        System.out.println("当前任务");
        Print.tasks(task);

        ActivitiFactory.complete(processInstance);
        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
        System.out.println("当前主流程任务：");
        Print.tasks(task);

        //主流程实例
        ProcessInstance piSub = runService.createProcessInstanceQuery().superProcessInstanceId(processInstance.getId()).singleResult();
        task = taskService.createTaskQuery().processInstanceId(piSub.getId()).list();
        System.out.println("当前子流程任务");
        Print.tasks(task);
    }


    @Test
    public void 事务子流程() {
        ProcessInstance processInstance = ActivitiFactory.deployAndStart("processes/_8BPMN事务子流程.bpmn20.xml");

        // 查询当前节点
        List<Task> task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
        System.out.println("当前任务");
        Print.tasks(task);

        ActivitiFactory.complete(processInstance);

        // 查询当前节点
        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
        System.out.println("当前任务");
        Print.tasks(task);
    }

    /**
     * 手动完成流转
     */
    @Test
    public void 特殊子流程() {
        ProcessInstance processInstance = ActivitiFactory.deployAndStart("processes/_8BPMN特殊子流程.bpmn20.xml");
        // 获取执行流
        Execution exe = runService.createExecutionQuery()
                .processInstanceId(processInstance.getId()).activityId("subprocess1").singleResult();

        // 让流程到达任务2
        runService.executeActivityInAdhocSubProcess(exe.getId(), "usertask2");
        // 查找任务2并完成
        Task subProcessTask2 = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .taskDefinitionKey("usertask2").singleResult();
        taskService.complete(subProcessTask2.getId());

        // 完成特别子流程
        runService.completeAdhocSubProcess(exe.getId());

        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        System.out.println("当前任务：" + task.getName());
    }

    @Test
    public void 条件顺序流() {
        ProcessInstance processInstance = ActivitiFactory.deployAndStart("processes/_8BPMN条件顺序流.bpmn20.xml");

        Map<String, Object> vars = new HashMap<>();
        vars.put("days", 4);

        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        System.out.println("当前任务：" + task.getName());
        // 完成第一个任务
        ActivitiFactory.complete(processInstance, vars);
    }

    @Test
    public void 并行网关() throws InterruptedException {
        ProcessInstance processInstance = ActivitiFactory.deployAndStart("processes/_8BPMN并行网关.bpmn20.xml");

        // 发送信号
        runService.signalEventReceived("mySignal");
        // 查询当前任务
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId())
                .singleResult();
        System.out.println("当前任务：" + task.getName());

        // 第二条流程实例，不发送消息，只等待定时器事件的触发
        ProcessInstance pi2 = runService.startProcessInstanceById(processInstance.getProcessDefinitionId());

        Thread.sleep(10000);

        task = taskService.createTaskQuery().processInstanceId(pi2.getId())
                .singleResult();
        System.out.println("当前任务：" + task.getName());
    }

    @Test
    public void 多实例流程活动() {
        List<String> datas1 = new ArrayList<>();
        datas1.add("a");
        datas1.add("b");
        datas1.add("c");
        datas1.add("d");
        Map<String, Object> vars = new HashMap<>();
        vars.put("datas1", datas1);
        ActivitiFactory.deployAndStart(vars, "processes/_8BPMN多实例流程活动.bpmn20.xml");


    }
}
