package cn.adbyte.activiti.feign.api.param;

import lombok.Data;

import java.util.List;

/**
 * @author Adam
 */
@Data
public abstract class TaskActionParam {

    private ActionType action;

    public static TaskActionParam.Complete createComplete(ActionType action, List<VariableParam> variables) {
        return new TaskActionParam.Complete(action, variables);
    }

    public TaskActionParam.Claim createClaim(ActionType action, String assignee) {
        return new TaskActionParam.Claim(action, assignee);
    }

    public TaskActionParam.Delegate createDelegate(ActionType action, String assignee) {
        return new TaskActionParam.Delegate(action, assignee);
    }

    public TaskActionParam.Resolve createResolve(ActionType action) {
        return new TaskActionParam.Resolve(action);
    }

    @Data
    public static class Complete extends TaskActionParam {
        private List<VariableParam> variables;

        Complete(ActionType action, List<VariableParam> variables) {
            super.action = action;
            this.variables = variables;
        }
    }

    @Data
    public static class Claim extends TaskActionParam {
        private String assignee;

        Claim(ActionType action, String assignee) {
            super.action = action;
            this.assignee = assignee;
        }
    }

    @Data
    public static class Delegate extends TaskActionParam {
        private String assignee;

        Delegate(ActionType action, String assignee) {
            super.action = action;
            this.assignee = assignee;
        }
    }

    @Data
    public static class Resolve extends TaskActionParam {
        Resolve(ActionType action) {
            super.action = action;
        }
    }

    public enum ActionType {
        /**
         * complete ｜ Claim ｜ delegate ｜ resolve a task
         */
        complete,
        claim,
        delegate,
        resolve
    }
}
