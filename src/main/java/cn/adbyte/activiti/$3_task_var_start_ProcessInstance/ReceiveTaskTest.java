package cn.adbyte.activiti.$3_task_var_start_ProcessInstance;

import cn.adbyte.activiti.Print;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ReceiveTaskTest {

    private static Logger logger = LoggerFactory.getLogger(ReceiveTaskTest.class);

    public static void main(String[] args) {
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        // 存储服务
        RepositoryService rs = engine.getRepositoryService();
        // 运行时服务
        RuntimeService runService = engine.getRuntimeService();
        // 任务服务
        TaskService taskService = engine.getTaskService();
        // 部署
        Deployment dep = rs.createDeployment().addClasspathResource("receiveTask.bpmn").deploy();
        ProcessDefinition pd = rs.createProcessDefinitionQuery().deploymentId(dep.getId()).singleResult();
        // 启动流程
        ProcessInstance pi = runService.startProcessInstanceById(pd.getId());
        // 查当前的子执行流（只有一个）
        Execution exe = runService.createExecutionQuery().processInstanceId(pi.getId()).onlyChildExecutions().singleResult();
        logger.warn("当前节点:");
        Print.exec(List.of(exe));

        // 等待任务，也就是说需要手动推进下一步的执行
        // 让它往前走
        runService.trigger(exe.getId());

        exe = runService.createExecutionQuery().processInstanceId(pi.getId()).onlyChildExecutions().singleResult();
        logger.warn("当前节点:");
        Print.exec(List.of(exe));
    }

}
