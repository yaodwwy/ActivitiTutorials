package cn.adbyte.activiti;

import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

public class Print {
    private static final Logger logger = LoggerFactory.getLogger(Print.class);
    public static void main(String[] args) {
        logger.info("Method:Hello");
        logger.warn("World");
    }

    public static void tasks(List<Task> tasks) {
        for (Task task : tasks) {
            logger.warn("task id =" + task.getId());
            logger.warn("name =" + task.getName());
            logger.warn("owner =" + task.getOwner());
            logger.warn("assignee =" + task.getAssignee());
            logger.warn("executionId =" + task.getExecutionId());
            logger.warn("TaskLocalVariables =" + task.getTaskLocalVariables());
            logger.warn("ProcessVariables =" + task.getProcessVariables());
            logger.warn("DelegationState =" + task.getDelegationState());
            logger.warn("=====================================");
        }
    }
    public static void instances(List<ProcessInstance> instances) {
        for (ProcessInstance pi : instances) {
            logger.warn("ProcessInstance id =" + pi.getId());
            logger.warn("name =" + pi.getName());
            logger.warn("BusinessKey =" + pi.getBusinessKey());
            logger.warn("ProcessVariables =" + pi.getProcessVariables());
            logger.warn("StartTime =" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(pi.getStartTime()));
            logger.warn("StartUserId =" + pi.getStartUserId());
            logger.warn("Description =" + pi.getDescription());
            logger.warn("ProcessDefinitionName =" + pi.getProcessDefinitionName());
            logger.warn("ProcessDefinitionKey =" + pi.getProcessDefinitionKey());
            logger.warn("=====================================");
        }
    }

    public static void exec(List<Execution> executions){
        for (Execution e : executions) {
            logger.warn("Execution id =" + e.getId());
            logger.warn("name =" + e.getName());
            logger.warn("ActivityId =" + e.getActivityId());
            logger.warn("ParentId =" + e.getParentId());
            logger.warn("ProcessInstanceId =" + e.getProcessInstanceId());
            logger.warn("RootProcessInstanceId =" + e.getRootProcessInstanceId());
            logger.warn("Description =" + e.getDescription());
            logger.warn("=====================================");
        }
    }
}
