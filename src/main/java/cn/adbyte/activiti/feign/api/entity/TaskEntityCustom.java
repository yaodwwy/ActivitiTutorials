package cn.adbyte.activiti.feign.api.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author Adam
 */
@Data
public class TaskEntityCustom {
    private String assignee;
    private Date createTime;
    private String delegationState;
    private String description;
    private Date dueDate;
    private String execution;
    private String id;
    private String name;
    private String owner;
    private String parentTask;
    private Integer priority;
    private String processDefinition;
    private String processInstance;
    private Boolean suspended;
    private String taskDefinitionKey;
    private String url;
    private String tenantId;
}
