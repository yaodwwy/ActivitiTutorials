package cn.adbyte.activiti;

import cn.adbyte.activiti.pojo.Person;
import org.activiti.engine.*;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ExecutionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

/**
 * 候选、签收、持有人测试类
 * 用户任务分类：
 * 分为4中状态：未签收/待办理、已签收/办理中、运行中/办理中、已完成/已办结
 */
@RunWith(SpringRunner.class)
@Import({ActivitiConfig.class})
public class _3任务Test {

    @Autowired
    private ActivitiFactory ActivitiFactory;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RuntimeService runService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private IdentityService identityService;

    private String random = UUID.randomUUID().toString().substring(0, 3);

    @Test
    public void 任务操作() {
        // 创建任务
        String taskId = "task" + random;
        Task task = taskService.newTask(taskId);
        task.setName("测试任务");
        taskService.saveTask(task);

        // 创建用户
        String userId = "user" + random;
        User user = identityService.newUser(userId);
        user.setFirstName("adam");
        identityService.saveUser(user);

        // 创建组
        String groupID = "group" + random;
        Group group = identityService.newGroup(groupID);
        group.setName("userGroup");
        group.setType("userGroupType");
        identityService.saveGroup(group);

        //创建用户关系
        identityService.createMembership(userId, groupID);

        // 设置任务的候选用户
        taskService.addCandidateUser(taskId, userId);
        // 设置任务的候选用户组
        taskService.addCandidateGroup(taskId, groupID);

        // 待签收/待办理
        List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(userId).list();
        System.out.println(userId + "这个用户有权限处理的任务有： ");
        Print.tasks(tasks);

        // 签收任务
        taskService.claim(taskId, userId);

        //按用户查询指派、签收的任务 用户待办
        tasks = taskService.createTaskQuery().taskAssignee(userId).list();
        System.out.println(userId + "这个用户有待办的任务有:");
        Print.tasks(tasks);

        // 签收人是持有人
        taskService.setOwner(taskId, userId);
        // assignee 受理人 是可以由持有人再委托

        //委托
        System.out.println("委托之前：");
        List<Task> list = taskService.createTaskQuery().taskOwner(userId).list();
        Print.tasks(list);
        taskService.delegateTask(task.getId(), "adam2");
        System.out.println("委托之后：");
        list = taskService.createTaskQuery().taskOwner(userId).list();
        Print.tasks(list);
        //结果：owner是userId，assignee是adam2
    }


    @Test
    public void 任务参数与附件() {
        Task task = taskService.newTask(UUID.randomUUID().toString());
        task.setName("参数测试任务");
        taskService.saveTask(task);
        taskService.setVariable(task.getId(), "var1", "hello");
        Map<String, Object> variables = taskService.getVariables(task.getId());
        System.out.println("文本参数: " + variables.toString());

        Person p = new Person();
        p.setId(1);
        p.setName("adam");
        // 对象参数
        taskService.setVariable(task.getId(), "person", p);
        Person pr = taskService.getVariable(task.getId(), "person", Person.class);
        System.out.println(pr.getId() + "---" + pr.getName());


        //任务Local变量
        Deployment dep = repositoryService.createDeployment().addClasspathResource("processes/_3任务变量.bpmn20.xml").deploy();
        ProcessDefinition pd = repositoryService.createProcessDefinitionQuery().deploymentId(dep.getId()).singleResult();
        ProcessInstance pi = runService.startProcessInstanceById(pd.getId());
        task = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
        //dataObject 流程配置文件变量
        String var = taskService.getVariable(task.getId(), "var", String.class);
        System.out.println("流程配置文件变量: " + var);

        taskService.setVariableLocal(task.getId(), "days", 3);
        System.out.println("任务Local变量完成前：" + task.getName() + ", days参数：" + taskService.getVariableLocal(task.getId(), "days"));

        taskService.complete(task.getId());

        task = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
        System.out.println("任务Local变量完成后：" + task.getName() + ", days参数：" + taskService.getVariableLocal(task.getId(), "days"));
    }
    @Test
    public void 参数作用域() {
        ProcessInstance processInstance = ActivitiFactory.deployAndStart("processes/_3任务参数作用域.bpmn20.xml");
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
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

        System.out.println("结束所有任务");
        for (Task task : tasks) {
            taskService.complete(task.getId());
        }

        Task taskC = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        ArrayList<Task> taskArrayList = new ArrayList<>();
        taskArrayList.add(taskC);
        Print.tasks(taskArrayList);
        System.out.println("taskVarA 的参数：" + String.valueOf(runService.getVariable(processInstance.getId(), "taskVarA")));
        System.out.println("taskVarB 的参数: " + String.valueOf(runService.getVariable(processInstance.getId(), "taskVarB")));
        System.out.println("流程实例: ");
        ArrayList<ProcessInstance> ProcessInstanceList = new ArrayList<>();
        ProcessInstanceList.add(processInstance);
        Print.instances(ProcessInstanceList);
    }
}
