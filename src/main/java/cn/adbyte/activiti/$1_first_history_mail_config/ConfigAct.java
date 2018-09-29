package cn.adbyte.activiti.$1_first_history_mail_config;

import org.activiti.engine.*;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

public class ConfigAct {

    public static void main(String[] args) throws Exception {
        ProcessEngineConfiguration config = ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration();
        /**
         * 数据源配置
         */
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/ACT");
        config.setJdbcDriver("org.postgresql.Driver");
        config.setJdbcUsername("root");
        config.setJdbcPassword("root");
        config.setDatabaseSchemaUpdate("true");
        /**
         * Mail配置
         */
        config.setMailServerHost("smtp.163.com");
        config.setMailServerPort(25);
        config.setMailServerDefaultFrom("abc@163.com");
        config.setMailServerUsername("abc@163.com");
        config.setMailServerPassword("123456");

        ProcessEngine engine = config.buildProcessEngine();
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
