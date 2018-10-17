package cn.adbyte.activiti.$5_开始事件_结束事件_边界事件;

import cn.adbyte.activiti.ActivitiConfig;
import cn.adbyte.activiti.Print;
import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author Adam
 */
@RunWith(SpringRunner.class)
@Import({ActivitiConfig.class})
public class 开始事件测试 {

    @Autowired
    private RepositoryService rs;

    @Autowired
    private RuntimeService runService;

    @Autowired
    private TaskService taskService;

    @Test
    public void 时间开始事件() throws InterruptedException {
        Deployment dep = rs.createDeployment().addClasspathResource("timer.bpmn").deploy();
        long dataCount = runService.createProcessInstanceQuery().count();
        System.out.println("sleep前流程实例数量：" + dataCount);
        for (int i = 0; i < 10; i++) {
            dataCount = runService.createProcessInstanceQuery().count();
            Thread.sleep(6000);
            System.out.println("sleep后流程实例数量：" + dataCount);
        }
    }

    @Test
    public void 消息开始事件() {
        Deployment dep = rs.createDeployment().addClasspathResource("message.bpmn").deploy();
        ProcessInstance pi = runService.startProcessInstanceByMessage("msgName");
        Print.instances(List.of(pi));
    }
    @Test
    public void 错误开始事件() {
        Deployment dep = rs.createDeployment().addClasspathResource("ThrowDelegate.bpmn").deploy();
        ProcessDefinition pd = rs.createProcessDefinitionQuery().deploymentId(dep.getId()).singleResult();
        ProcessInstance pi = runService.startProcessInstanceById(pd.getId());
        Print.instances(List.of(pi));
    }

}
