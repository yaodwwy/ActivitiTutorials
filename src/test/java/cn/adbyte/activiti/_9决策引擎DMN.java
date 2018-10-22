package cn.adbyte.activiti;

import org.activiti.dmn.api.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;


@RunWith(SpringRunner.class)
@Import({ActivitiDMNConfig.class})
public class _9决策引擎DMN {

    @Autowired
    private DmnRepositoryService dmnRepositoryService;
    @Autowired
    private DmnRuleService dmnRuleService;

    @Test
    public void test() {

        // 进行规则 部署
        DmnDeployment dep = dmnRepositoryService.createDeployment().addClasspathResource("dmn/_10第一个DMN.dmn").deploy();
        // 进行数据查询
        DmnDecisionTable dmnDecisionTable = dmnRepositoryService.createDecisionTableQuery()
                .deploymentId(dep.getId()).singleResult();
        // 初始化参数
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("personAge", 19);
        // 传入参数执行决策，并返回结果
        RuleEngineExecutionResult result = dmnRuleService.executeDecisionByKey(dmnDecisionTable.getKey(), params);
        // 控制台输出结果
        System.out.println(result.getResultVariables().get("myResult"));
        // 重新设置参数
        params.put("personAge", 5);
        // 重新执行决策
        result = dmnRuleService.executeDecisionByKey(dmnDecisionTable.getKey(), params);
        // 控制台重新输出结果
        System.out.println(result.getResultVariables().get("myResult"));
    }
}
