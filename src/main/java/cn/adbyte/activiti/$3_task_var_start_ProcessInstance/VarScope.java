package cn.adbyte.activiti.$3_task_var_start_ProcessInstance;

import java.util.List;

import cn.adbyte.activiti.Print;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ExecutionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 并行流程
 * @author Adam
 */
public class VarScope {

    private static Logger logger = LoggerFactory.getLogger(VarScope.class);

    public static void main(String[] args) {
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        // 存储服务
        RepositoryService rs = engine.getRepositoryService();
        // 运行时服务
        RuntimeService runService = engine.getRuntimeService();
        // 任务服务
        TaskService taskService = engine.getTaskService();
        // 部署
        Deployment dep = rs.createDeployment().addClasspathResource("scope.bpmn").deploy();
        ProcessDefinition pd = rs.createProcessDefinitionQuery().deploymentId(dep.getId()).singleResult();
        // 启动流程
        ProcessInstance pi = runService.startProcessInstanceById(pd.getId(), "businessKeyVal");

        List<Task> tasks = taskService.createTaskQuery().processInstanceId(pi.getId()).list();
        Print.tasks(tasks);
        for (Task task : tasks) {
            ExecutionQuery executionQuery = runService.createExecutionQuery();
            Execution exe = executionQuery.executionId(task.getExecutionId()).singleResult();
            //在执行流里设置任务参数
            if ("TaskA".equals(task.getName())) {
                runService.setVariableLocal(exe.getId(), "taskVarA", "varA");
            } else {
                runService.setVariable(exe.getId(), "taskVarB", "varB");
            }
        }

        //结束所有任务
        for (Task task : tasks) {
            taskService.complete(task.getId());
        }

        Task taskC = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
        Print.tasks(List.of(taskC));
        logger.warn("taskVarA 的参数：" + String.valueOf(runService.getVariable(pi.getId(), "taskVarA")));
        logger.warn("taskVarB 的参数: " + String.valueOf(runService.getVariable(pi.getId(), "taskVarB")));
        logger.warn("流程实例: ");
        Print.instances(List.of(pi));
    }

}
