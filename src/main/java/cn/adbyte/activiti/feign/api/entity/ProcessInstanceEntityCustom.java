package cn.adbyte.activiti.feign.api.entity;

import cn.adbyte.activiti.feign.api.param.VariableParam;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author Adam
 */
@Data
public class ProcessInstanceEntityCustom {

    private String id;
    private String url;
    private String businessKey;
    private Boolean suspended;
    private Boolean ended;
    private String processDefinitionId;
    private String processDefinitionUrl;
    private String processDefinitionKey;
    private String activityId;
    private List<VariableParam> variables;
    private String tenantId;
    private String name;
    private Boolean completed;
}
