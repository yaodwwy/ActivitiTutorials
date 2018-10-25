package cn.adbyte.activiti.feign.api.param;

import lombok.Data;

/**
 * @author Adam
 */
@Data
public class VariableParam {
    private String name;
    private String value;
    private String valueUrl;
    private String scope;
    private String type;
}
