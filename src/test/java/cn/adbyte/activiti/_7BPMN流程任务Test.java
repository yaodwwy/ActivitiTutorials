package cn.adbyte.activiti;

import cn.adbyte.activiti.delegate.EventDelegate;
import cn.adbyte.activiti.pojo.MyBean;
import cn.adbyte.activiti.service.AuthService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

/**
 * 其它任务
 * 手工任务 (抛出)
 * 接收任务 (捕获)RuntimeService.trigger
 * 邮件任务
 * Mule任务
 * 业务规则任务
 */
@RunWith(SpringRunner.class)
@Import({ActivitiConfig.class})
public class _7BPMN流程任务Test {

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

    private String random = UUID.randomUUID().toString().substring(0, 3);

    @Test
    public void 用户任务权限() {
        // 创建用户组
        if (identityService.createGroupQuery().groupId("boss").count() == 0) {
            Group bossG = identityService.newGroup("boss");
            bossG.setName("boss");
            identityService.saveGroup(bossG);
        }

        if (identityService.createGroupQuery().groupId("management").count() == 0) {
            Group mangG = identityService.newGroup("management");
            mangG.setName("management");
            identityService.saveGroup(mangG);
        }

        if (identityService.createUserQuery().userId("angus").count() == 0) {
            User user = identityService.newUser("angus");
            user.setFirstName("Angus");
            identityService.saveUser(user);
        }

        ActivitiFactory.deployAndStart("processes/_7BPMN用户任务权限.bpmn20.xml");

        // 查询各个用户下面有权限看到的任务
        List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup("boss").list();
        System.out.println("boss用户组，可以认领的任务：");
        Print.tasks(tasks);

        tasks = taskService.createTaskQuery().taskCandidateGroup("management").list();
        System.out.println("management用户组，可以认领的任务：");
        Print.tasks(tasks);

        tasks = taskService.createTaskQuery().taskCandidateUser("angus").list();
        System.out.println("angus用户，可以认领的任务：");
        Print.tasks(tasks);

        tasks = taskService.createTaskQuery().taskCandidateUser("adam").list();
        System.out.println("adam用户，可以认领的任务：");
        Print.tasks(tasks);

    }

    @Test
    public void JUEL表达式动态代理人() {

        if (identityService.createUserQuery().userId("userA").count() == 0) {
            User userA = identityService.newUser("userA");
            userA.setFirstName("Angus");
            identityService.saveUser(userA);
        }

        if (identityService.createUserQuery().userId("userB").count() == 0) {
            User userB = identityService.newUser("userB");
            userB.setFirstName("Angus");
            identityService.saveUser(userB);
        }

        Deployment dep = repositoryService.createDeployment().addClasspathResource("processes/_7BPMN用户任务JUEL表达式.bpmn20.xml").deploy();
        ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
                .deploymentId(dep.getId()).singleResult();

        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("authService", new AuthService());
        runService.startProcessInstanceById(pd.getId(), vars);

        // 查询各个用户下面有权限看到的任务

        List<Task> tasks = taskService.createTaskQuery().taskCandidateUser("userA").list();
        System.out.println("userA 用户，可以认领的任务：");
        Print.tasks(tasks);

        tasks = taskService.createTaskQuery().taskCandidateUser("userB").list();
        System.out.println("userB 用户，可以认领的任务：");
        Print.tasks(tasks);
    }

    @Test
    public void 服务任务委托表达式() {
        EventDelegate delegate = new EventDelegate();
        Map<String, Object> vars = new HashMap<>(1);
        vars.put("myDelegate", delegate);
        ActivitiFactory.deployAndStart(vars, "processes/_7BPMN服务任务委托表达式.bpmn20.xml");
    }

    /**
     * 流程文件里注入bean在流程文件里定义变量代码里取值
     */
    @Test
    public void 服务任务JUEL表达式赋值取值测试() {
        Map<String, Object> vars = new HashMap<>(1);
        vars.put("myBean", new MyBean());
        ProcessInstance processInstance = ActivitiFactory.deployAndStart(vars, "processes/_7BPMN服务任务JUEL表达式.bpmn20.xml");
        System.out.println("取到的参数值：" + runService.getVariable(processInstance.getId(), "myName"));
    }

    @Test
    public void JavaShell任务() throws IOException {
        //创建命令集合
        List<String> argList = new ArrayList<String>();
        argList.add("cmd");
        argList.add("/c");
        argList.add("echo");
        argList.add("hello");
        argList.add("crazyit");
        ProcessBuilder processBuilder = new ProcessBuilder(argList);
        // 执行命令返回进程
        Process process = processBuilder.start();
        // 解析输出
        String result = IOUtils.toString(process.getInputStream(), Charset.forName("utf-8"));
        System.out.println(result);
    }

    @Test
    public void BPMNShell任务() {
        ProcessInstance processInstance = ActivitiFactory.deployAndStart("processes/_7BPMN服务任务Shell任务.bpmn20.xml");
        System.out.println("返回结果：" + runService.getVariable(processInstance.getId(), "javaHome"));
    }

    @Test
    public void BPMN任务监听器() {
        ProcessInstance processInstance = ActivitiFactory.deployAndStart("processes/_7BPMN任务监听器.bpmn20.xml");
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        taskService.complete(task.getId());
    }


}
