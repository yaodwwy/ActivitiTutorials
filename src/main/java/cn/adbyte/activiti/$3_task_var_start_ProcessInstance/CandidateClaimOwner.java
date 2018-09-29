package cn.adbyte.activiti.$3_task_var_start_ProcessInstance;

import java.util.List;
import java.util.UUID;

import cn.adbyte.activiti.Print;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.TaskService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 候选、签收、持有人测试类
 * 用户任务分类：
 * 分为4中状态：未签收/待办理、已签收/办理中、运行中/办理中、已完成/已办结
 */
public class CandidateClaimOwner {

    private static Logger logger = LoggerFactory.getLogger(CandidateClaimOwner.class);

    public static void main(String[] args) {
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = engine.getTaskService();
        IdentityService is = engine.getIdentityService();
        // 创建任务
        String taskId = UUID.randomUUID().toString();
        Task task = taskService.newTask(taskId);
        task.setName("测试任务");
        taskService.saveTask(task);

        // 创建用户
        String userId = UUID.randomUUID().toString();
        User user = is.newUser(userId);
        user.setFirstName("adam");
        is.saveUser(user);

        // 创建组
        String groupID = UUID.randomUUID().toString();
        Group group = is.newGroup(groupID);
        group.setName("userGroup");
        group.setType("userGroupType");
        is.saveGroup(group);

        // 设置任务的候选用户
        taskService.addCandidateUser(taskId, userId);

        // 待签收/待办理
        List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(userId).list();
        logger.warn(userId+"这个用户有权限处理的任务有： ");
        Print.tasks(tasks);

        // 签收任务
        taskService.claim(taskId, userId);

        //按用户查询指派、签收的任务 用户待办
        tasks = taskService.createTaskQuery().taskAssignee(userId).list();
        logger.warn(userId+ "这个用户有待办的任务有:");
        Print.tasks(tasks);

        // 签收人是持有人
        taskService.setOwner(taskId, userId);
        // assignee 受理人 是可以由持有人再委托

        //委托
        logger.warn("委托之前：");
        List<Task> list = taskService.createTaskQuery().taskOwner(userId).list();
        Print.tasks(list);
        taskService.delegateTask(task.getId(),"adam2");
        logger.warn("委托之后：");
        list = taskService.createTaskQuery().taskOwner(userId).list();
        Print.tasks(list);
        //结果：owner是userId，assignee是adam2

    }

}
