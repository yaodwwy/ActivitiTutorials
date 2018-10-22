package cn.adbyte.activiti;

import org.activiti.engine.*;
import org.activiti.engine.runtime.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * 工作的产生与管理
 * 异步任务
 * 定时事件
 * 暂停的工作
 * 无法执行的工作
 */
@RunWith(SpringRunner.class)
@Import({ActivitiConfig.class})
public class _4流程操作Test {

    @Autowired
    private ActivitiFactory ActivitiFactory;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runService;

    @Test
    public void 捕获信号事件触发流程继续() {
        ProcessInstance processInstance = ActivitiFactory.deployAndStart("processes/_4捕获信号事件触发流程继续.bpmn20.xml");

        // 查当前的子执行流（只有一个）
        Execution exe = runService.createExecutionQuery()
                .processInstanceId(processInstance.getId()).onlyChildExecutions()
                .singleResult();

        System.out.println("当前节点：");
        Print.exec(List.of(exe));

        runService.signalEventReceived("testSignal");

        exe = runService.createExecutionQuery()
                .processInstanceId(processInstance.getId()).onlyChildExecutions()
                .singleResult();

        System.out.println("当前节点：");
        Print.exec(List.of(exe));
    }

    @Test
    public void 捕获消息事件触发流程继续() {
        ProcessInstance processInstance = ActivitiFactory.deployAndStart("processes/_4捕获消息事件触发流程继续.bpmn20.xml");
//        MessageEvent.bpmn
        // 查当前的子执行流（只有一个）
        Execution exe = runService.createExecutionQuery()
                .processInstanceId(processInstance.getId()).onlyChildExecutions()
                .singleResult();
        System.out.println("当前节点：");
        Print.exec(List.of(exe));

        // 一个消息触发的中间捕获事件
        // 让它往前走
        runService.messageEventReceived("testMsg", exe.getId());

        exe = runService.createExecutionQuery()
                .processInstanceId(processInstance.getId()).onlyChildExecutions()
                .singleResult();
        System.out.println("当前节点：");
        Print.exec(List.of(exe));
    }

    @Test
    public void 接收任务触发流程继续() {
        ProcessInstance processInstance = ActivitiFactory.deployAndStart("processes/_4接收任务触发流程继续.bpmn20.xml");
        // 查当前的子执行流（只有一个）
        Execution exe = runService.createExecutionQuery().processInstanceId(processInstance.getId()).onlyChildExecutions().singleResult();
        System.out.println("当前节点:");
        Print.exec(List.of(exe));

        // 等待任务，也就是说需要手动推进下一步的执行
        // 让它往前走
        runService.trigger(exe.getId());

        exe = runService.createExecutionQuery().processInstanceId(processInstance.getId()).onlyChildExecutions().singleResult();
        System.out.println("当前节点:");
        Print.exec(List.of(exe));
    }

    @Test
    public void 流程操作() throws InterruptedException {
        ProcessInstance processInstance = ActivitiFactory.deployAndStart("processes/_4流程操作.bpmn20.xml");
        String processInstanceID = processInstance.getId();
        Print.instances(List.of(processInstance));
        System.out.println("==================等20秒以上再关！等下会有异常处理类打印==================");

        /*
        ISO_8601格式：（P ,Y,M,W,D,T,.H,M,S）
        当一个流程被挂起后,是不能继续新建立这个流程的实例了,会有异常抛出,请注意在上面的方法中,
        可以设定这个流程实例的过期时间,也可以通过流程实例id去挂起激活流程:
         */
        // 中止
        runService.suspendProcessInstanceById(processInstanceID);
        Thread.sleep(19_000);
        // 再激活
        runService.activateProcessInstanceById(processInstanceID);
    }

}
