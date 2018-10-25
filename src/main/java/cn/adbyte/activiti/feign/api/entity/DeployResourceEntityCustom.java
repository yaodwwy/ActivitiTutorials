package cn.adbyte.activiti.feign.api.entity;

import lombok.Data;

/**
 * @author Adam
 */
@Data
public class DeployResourceEntityCustom {
    private String id;
    private String url;
    private String contentUrl;
    private String mediaType;
    private String type;
}
