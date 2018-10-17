package cn.adbyte.activiti.$4_service_timer_suspend_exception;

import cn.adbyte.activiti.ActivitiConfig;
import cn.adbyte.activiti.Print;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.*;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

public class ServiceTaskTest {

    public static void main(String[] args) throws InterruptedException {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(ActivitiConfig.class);
        RepositoryService rs = applicationContext.getBean(RepositoryService.class);
        RuntimeService runService = applicationContext.getBean(RuntimeService.class);
        ManagementService managementService = applicationContext.getBean(ManagementService.class);
        TaskService taskService = applicationContext.getBean(TaskService.class);

        Deployment dep = rs.createDeployment().addClasspathResource("service_timer_suspend_exception.bpmn").deploy();
        ProcessDefinition pd = rs.createProcessDefinitionQuery().deploymentId(dep.getId()).singleResult();

        ProcessInstance pi = runService.startProcessInstanceById(pd.getId());
        Print.instances(List.of(pi));
        System.out.println("==================等20秒以上再关！等下会有异常处理类打印==================");

        /*
        ISO_8601格式：（P ,Y,M,W,D,T,.H,M,S）
        当一个流程被挂起后,是不能继续新建立这个流程的实例了,会有异常抛出,请注意在上面的方法中,
        可以设定这个流程实例的过期时间,也可以通过流程实例id去挂起激活流程:
         */
        // 中止
        runService.suspendProcessInstanceById(pi.getId());
        Thread.sleep(10000);
        // 再激活
        runService.activateProcessInstanceById(pi.getId());

        //工作查询对象
        JobQuery jobQuery = managementService.createJobQuery();
        DeadLetterJobQuery deadLetterJobQuery = managementService.createDeadLetterJobQuery();
        SuspendedJobQuery suspendedJobQuery = managementService.createSuspendedJobQuery();
        TimerJobQuery timerJobQuery = managementService.createTimerJobQuery();
        //工作的转移
        //managementService.moveJobToDeadLetterJob("")
        //工作的删除
        //managementService.deleteJob("");
        List<Job> list = jobQuery.list();
        list.forEach(job -> {
            //重试次数，如果超过，自动把JOB放到act_ru_deadletter_job表
            managementService.setJobRetries(job.getId(), 1);
            //手动执行job 会忽略配置异步执行
            managementService.executeJob(job.getId());
        });

        //
    }

}
