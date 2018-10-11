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
public class 结束事件测试 {

    @Autowired
    private RepositoryService rs;

    @Autowired
    private RuntimeService runService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ManagementService managementService;


    @Test
    public void 错误结束事件() {
        Deployment dep = rs.createDeployment().addClasspathResource("ThrowDelegateByEndEvant.bpmn20.xml").deploy();
        ProcessDefinition pd = rs.createProcessDefinitionQuery().deploymentId(dep.getId()).singleResult();
        ProcessInstance pi = runService.startProcessInstanceById(pd.getId());
        Print.instances(List.of(pi));
        System.out.println("错误结束事件：结束时触发...");
    }

    @Test
    public void 取消结束事件() {
        Deployment dep = rs.createDeployment().addClasspathResource("Cancle.bpmn20.xml").deploy();
        ProcessDefinition pd = rs.createProcessDefinitionQuery().deploymentId(dep.getId()).singleResult();
        ProcessInstance pi = runService.startProcessInstanceById(pd.getId());

        Task task = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
        System.out.println("当前流程任务：");
        Print.tasks(List.of(task));
        taskService.complete(task.getId());

        task = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
        System.out.println("当前流程任务：");
        Print.tasks(List.of(task));
    }


    @Test
    public void 终结结束事件() {
        Deployment dep = rs.createDeployment().addClasspathResource("terminal.bpmn").deploy();
        ProcessDefinition pd = rs.createProcessDefinitionQuery().deploymentId(dep.getId()).singleResult();

        ProcessInstance pi = runService.startProcessInstanceById(pd.getId());

        List<Execution> list = runService.createExecutionQuery().processInstanceId(pi.getId()).list();
        System.out.println("终结前执行流：");
        Print.exec(list);

        List<Task> tasks = taskService.createTaskQuery().processInstanceId(pi.getId()).list();
        for (Task task : tasks) {
            if (task.getName().equals("Task1")) {
                taskService.complete(task.getId());
            }
        }

        list = runService.createExecutionQuery().processInstanceId(pi.getId()).list();
        System.out.println("终结后执行流数量：");

        Print.exec(list);

        ProcessInstance newPi = runService.createProcessInstanceQuery().processInstanceId(pi.getId()).singleResult();
        System.out.println("流程实例已终结，Task3不可达！");
    }
}
