package cn.adbyte.activiti.$3_task_var_start_ProcessInstance;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;

public class MessageTest {

    public static void main(String[] args) {
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        // 存储服务
        RepositoryService rs = engine.getRepositoryService();
        // 运行时服务
        RuntimeService runService = engine.getRuntimeService();
        // 任务服务
        TaskService taskService = engine.getTaskService();
        // 部署
        Deployment dep = rs.createDeployment()
                .addClasspathResource("MessageEvent.bpmn").deploy();
        ProcessDefinition pd = rs.createProcessDefinitionQuery()
                .deploymentId(dep.getId()).singleResult();
        // 启动流程
        ProcessInstance pi = runService.startProcessInstanceById(pd.getId());
        // 查当前的子执行流（只有一个）
        Execution exe = runService.createExecutionQuery()
                .processInstanceId(pi.getId()).onlyChildExecutions()
                .singleResult();

        System.out.println(pi.getId() + ", 当前节点：" + exe.getActivityId());

        // 一个消息触发的中间捕获事件
        // 让它往前走
        runService.messageEventReceived("testMsg", exe.getId());
        
        exe = runService.createExecutionQuery()
                .processInstanceId(pi.getId()).onlyChildExecutions()
                .singleResult();
        System.out.println(pi.getId() + ", 当前节点：" + exe.getActivityId());
    }

}
