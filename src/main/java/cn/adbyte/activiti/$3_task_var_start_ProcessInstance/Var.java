package cn.adbyte.activiti.$3_task_var_start_ProcessInstance;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import cn.adbyte.activiti.$3_task_var_start_ProcessInstance.pojo.Person;
import cn.adbyte.activiti.Print;
import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Var {

    private static Logger logger = LoggerFactory.getLogger(Var.class);

    public static void main(String[] args) {
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        TaskService ts = engine.getTaskService();


        Task task = ts.newTask(UUID.randomUUID().toString());
        task.setName("参数测试任务");
        ts.saveTask(task);
        //文本参数
        ts.setVariable(task.getId(), "var1", "hello");

        Map<String, Object> variables = ts.getVariables(task.getId());
        logger.warn(variables.toString());


        Person p = new Person();
        p.setId(1);
        p.setName("adam");
        // 对象参数
        ts.setVariable(task.getId(), "person", p);

        Person pr = ts.getVariable(task.getId(), "person", Person.class);
        logger.warn(pr.getId() + "---" + pr.getName());


        //任务Local变量
        
        RepositoryService rs = engine.getRepositoryService();
        RuntimeService runService = engine.getRuntimeService();
        Deployment dep = rs.createDeployment().addClasspathResource("var.bpmn").deploy();
        ProcessDefinition pd = rs.createProcessDefinitionQuery().deploymentId(dep.getId()).singleResult();

        ProcessInstance pi = runService.startProcessInstanceById(pd.getId());

        task = ts.createTaskQuery().processInstanceId(pi.getId()).singleResult();
        //dataObject 流程配置文件变量
        String var = ts.getVariable(task.getId(), "var", String.class);
        logger.warn("流程配置文件变量: " + var);

        ts.setVariableLocal(task.getId(), "days", 3);
        logger.warn("任务Local变量完成前：" + task.getName() + ", days参数：" + ts.getVariableLocal(task.getId(), "days"));

        ts.complete(task.getId());

        task = ts.createTaskQuery().processInstanceId(pi.getId()).singleResult();
        logger.warn("任务Local变量完成后：" + task.getName() + ", days参数：" + ts.getVariableLocal(task.getId(), "days"));





    }

}
