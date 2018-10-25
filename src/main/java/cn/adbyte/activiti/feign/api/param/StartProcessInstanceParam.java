package cn.adbyte.activiti.feign.api.param;

import lombok.Data;

import java.util.List;

/**
 * 根据不同的参数来启动实例
 *
 * @author Adam
 */
@Data
public abstract class StartProcessInstanceParam {

    private String businessKey;
    private List<VariableParam> variables;

    public static ById createId(String processDefinitionId, String businessKey, List<VariableParam> variables) {
        return new ById(processDefinitionId, businessKey, variables);
    }

    public static ByKey createKey(String processDefinitionKey, String businessKey, String tenantId, List<VariableParam> variables) {
        return new ByKey(processDefinitionKey, businessKey, tenantId, variables);
    }

    public static ByMessage createMessage(String message, String businessKey, String tenantId, List<VariableParam> variables) {
        return new ByMessage(message, businessKey, tenantId, variables);
    }


    @Data
    public static class ById extends StartProcessInstanceParam {
        private String processDefinitionId;

        public ById(String processDefinitionId, String businessKey, List<VariableParam> variables) {
            super.businessKey = businessKey;
            super.variables=variables;
            this.processDefinitionId = processDefinitionId;
        }
    }

    @Data
    public static class ByKey extends StartProcessInstanceParam {
        private String processDefinitionKey;
        private String tenantId;

        public ByKey(String processDefinitionKey, String businessKey, String tenantId, List<VariableParam> variables) {
            super.businessKey = businessKey;
            super.variables=variables;
            this.tenantId = tenantId;
            this.processDefinitionKey = processDefinitionKey;
        }
    }

    @Data
    public static class ByMessage extends StartProcessInstanceParam {
        private String message;
        private String tenantId;

        public ByMessage(String message, String businessKey, String tenantId, List<VariableParam> variables) {
            super.businessKey = businessKey;
            super.variables = variables;
            this.tenantId = tenantId;
            this.message = message;
        }
    }
}

