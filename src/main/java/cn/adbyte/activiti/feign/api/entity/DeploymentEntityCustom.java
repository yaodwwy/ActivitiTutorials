package cn.adbyte.activiti.feign.api.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author Adam
 */
@Data
public class DeploymentEntityCustom {
    private String id;
    private String name;
    private Date deploymentTime;
    private String category;
    private String url;
    private String tenantId;
}
