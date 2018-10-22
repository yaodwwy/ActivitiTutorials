package cn.adbyte.activiti;

import org.activiti.engine.ManagementService;
import org.activiti.engine.runtime.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@Import({ActivitiConfig.class})
public class _5管理服务Test {

    @Autowired
    private ManagementService managementService;

    @Test
    public void 工作任务常用操作() {

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
    }
}
