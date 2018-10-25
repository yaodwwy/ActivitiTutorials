package cn.adbyte.activiti;

import cn.adbyte.activiti.pojo.Member;
import cn.adbyte.activiti.pojo.Person;
import org.activiti.dmn.api.*;
import org.activiti.dmn.engine.DmnEngine;
import org.activiti.dmn.engine.DmnEngineConfiguration;
import org.junit.Test;
import org.junit.internal.runners.TestMethod;
import org.junit.runner.RunWith;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.Serializable;
import java.lang.reflect.Method;
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
    public void Hello() {

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
        // params.put("personAge", 5);
        // 重新执行决策
        // result = dmnRuleService.executeDecisionByKey(dmnDecisionTable.getKey(), params);
        // 控制台重新输出结果
        // System.out.println(result.getResultVariables().get("myResult"));
    }

    @Test
    public void MVEL() {
        // 编辑表达式
        Serializable compiledExpression = MVEL.compileExpression("person.age >= 18");
        Map<String, Object> vars = new HashMap<String, Object>();
        Person p = new Person();
        p.setAge(17);
        vars.put("person", p);
        // 执行表达式并返回结果
        Boolean result = MVEL.executeExpression(compiledExpression, vars, Boolean.class);
        System.out.println(result);
    }

    @Test
    public void MVEL方法解析() {
        Method m = getMethod(this.getClass(), "testMethod", String.class, Integer.class);

        // 创建解析上下文对象
        ParserContext parserContext = new ParserContext();
        // 添加方法导入
        parserContext.addImport("fn_abc", m);

        Serializable compiledExpression = MVEL.compileExpression("fn_abc('angus', 33)", parserContext);

        String result = MVEL.executeExpression(compiledExpression, null, String.class);
        System.out.println(result);
    }

    public static String testMethod(String name, Integer age) {
        return "名称：" + name + ", 年龄：" + age;
    }

    public static Method getMethod(Class classRef, String methodName,
                                   Class... methodParm) {
        try {
            return classRef.getMethod(methodName, methodParm);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Test
    public void MVEL销售案例一() {
        DmnDeployment deploy = dmnRepositoryService.createDeployment()
                .addClasspathResource("dmn/_10使用MVEL.dmn").deploy();
        // 根据部署对象查询决策表
        DmnDecisionTable dmnDecisionTable = dmnRepositoryService.createDecisionTableQuery()
                .deploymentId(deploy.getId()).singleResult();
        // 设置参数
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("personName", "Angus");
        vars.put("age", 33);
        // 运行决策表
        RuleEngineExecutionResult result = dmnRuleService.executeDecisionByKey(dmnDecisionTable.getKey(), vars);
        System.out.println(result.getResultVariables().get("outputResult"));
    }

    @Test
    public void MVEL销售案例二() {
        DmnDeployment deploy = dmnRepositoryService.createDeployment()
                .addClasspathResource("dmn/_10折扣测试.dmn").deploy();
        // 根据部署对象查询决策表
        DmnDecisionTable dt = dmnRepositoryService.createDecisionTableQuery()
                .deploymentId(deploy.getId()).singleResult();

        Member member = new Member();
        member.setIdentity("gold");
        member.setAmount(100);

        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("member", member);

        RuleEngineExecutionResult result = dmnRuleService.executeDecisionByKey(
                dt.getKey(), vars);

        Double resultMoney = (Double) result.getResultVariables().get(
                "resultMoney");
        System.out.println(resultMoney);
    }
}
