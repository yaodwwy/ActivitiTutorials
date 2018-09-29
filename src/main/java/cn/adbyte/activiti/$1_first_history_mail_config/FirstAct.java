package cn.adbyte.activiti.$1_first_history_mail_config;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

public class FirstAct {

    public static void main(String[] args) throws Exception {
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        // 存储服务
        RepositoryService rs = engine.getRepositoryService();
        // 运行时服务
        RuntimeService runService = engine.getRuntimeService();
        // 任务服务
        TaskService taskService = engine.getTaskService();
        
        rs.createDeployment().addClasspathResource("first.bpmn").deploy();

        ProcessInstance pi = runService.startProcessInstanceByKey("myProcess");
        
        // 普通员工完成请假的任务
        Task task = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
        System.out.println("当前流程节点：" + task.getName() +", Assignee:"+ task.getAssignee());
        taskService.complete(task.getId());
        
        // 经理审核任务
        task = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
        System.out.println("当前流程节点：" + task.getName());
        taskService.complete(task.getId());
        
        task = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
        System.out.println("流程结束后：" + task);
        
        engine.close();
        System.exit(0);
    }

}
