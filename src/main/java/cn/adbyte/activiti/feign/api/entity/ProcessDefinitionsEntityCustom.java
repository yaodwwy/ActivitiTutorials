package cn.adbyte.activiti.feign.api.entity;

import lombok.Data;

/**
 * @author Adam
 */
@Data
public class ProcessDefinitionsEntityCustom {

    private String id;
    private String url;
    private String key;
    private Integer version;
    private String name;
    private String description;
    private String tenantId;
    private String deploymentId;
    private String deploymentUrl;
    private String resource;
    private String diagramResource;
    private String category;
    private Boolean graphicalNotationDefined;
    private Boolean suspended;
    private Boolean startFormDefined;
}
