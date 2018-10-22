package cn.adbyte.activiti;

import cn.adbyte.activiti.pojo.Member;
import cn.adbyte.activiti.pojo.Message;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.definition.rule.Rule;
import org.kie.api.io.KieResources;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.AgendaFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;


@RunWith(SpringRunner.class)
@Import({ActivitiConfig.class})
public class _9规则引擎Drools {

    @Autowired
    private ActivitiFactory ActivitiFactory;

    @Autowired
    private TaskService taskService;

    protected KieServices kieServices;
    protected KieContainer kieContainer;
    protected KieRepository repository;

    @Before
    public void setUp() {
        // 通过KieServices对象得到一个KieContainer，利用kieContainer对象创建一个新的KieSession
        // KieServices就是一个中心，通过它来获取的各种对象来完成规则构建、管理和执行等操作
        kieServices = KieServices.Factory.get();
        // KieContainer就是一个KieBase的容器
        // KieBase就是一个知识仓库，包含了若干的规则、流程、方法等
        kieContainer = kieServices.getKieClasspathContainer();
        /** 创建KieBase是一个成本非常高的事情，KieBase会建立知识（规则、流程）仓库
         * KieRepository是一个单例对象，它是一个存放KieModule的仓库
         * KieProject：
         * KieContainer通过KieProject来初始化、构造KieModule，
         * 并将KieModule存放到KieRepository中，然后KieContainer可以通过KieProject来查找KieModule定义的信息，
         * 并根据这些信息构造KieBase和KieSession。
         * */
        repository = kieServices.getRepository();
    }

    @Test
    public void HelloWorld() {
        KieSession kSession = kieContainer.newKieSession("ksession-rules");
        Message message = new Message();
        message.setMessage("Hello World");
        message.setStatus(Message.HELLO);
        kSession.insert(message);
        kSession.fireAllRules();
    }

    @Test
    public void 从配置文件kmodule获取Session() {
        // KieSession则是一个成本非常低的事情
        // KieSession就是一个跟Drools引擎打交道的会话，其基于KieBase创建
        // 包含运行时数据，包含“事实 Fact” 本质上是从KieBase中创建出来
        // KieSession就是应用程序跟规则引擎进行交互的会话通道
        KieSession kSession = kieContainer.newKieSession("simpleRuleKSession");

        // 定义一个事实对象
        Member m = new Member();
        m.setIdentity("copper");

        assertNotNull("session 是 null 需要检查 kmodule.xml配置是否正确！", kSession);

        // 通过kSession.insert方法来将事实（Fact）插入到引擎中，也就是Working Memory中
        kSession.insert(m);
        // 通过kSession.fireAllRules方法来通知规则引擎执行规则
        kSession.fireAllRules();
        // 关闭当前session的资源
        kSession.dispose();
    }

    @Test
    public void 以编码方式完成kmodule的定义() {

        KieResources resources = kieServices.getResources();
        // 创建KieModuleModel
        KieModuleModel kieModuleModel = kieServices.newKieModuleModel();
        // 创建 KieSessionModel
        KieBaseModel baseModel = kieModuleModel.newKieBaseModel("SimpleRuleKBase").addPackage("drools");
        // 创建完成之后可以生产一个xml文件，就是kmodule.xml文件了
        baseModel.newKieSessionModel("simpleRuleKSession");
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();

        String xml = kieModuleModel.toXML();
        // 将这个xml文件写入到KieFileSystem中
        System.out.println(xml);
        // 然后将规则文件等写入到KieFileSystem中
        kieFileSystem.writeKModuleXML(xml);
        // 最后通过KieBuilder进行构建就将该kmodule加入到KieRepository中了。
        // 这样就将自定义的kmodule加入到引擎中了，就可以按照之前的方法进行使用了
        kieFileSystem.write("src/main/resources/drools/", resources.newClassPathResource("drools/"));

        KieBuilder kb = kieServices.newKieBuilder(kieFileSystem);
        kb.buildAll();//7
        if (kb.getResults().hasMessages(org.kie.api.builder.Message.Level.ERROR)) {
            throw new RuntimeException("Build Errors:\n"
                    + kb.getResults().toString());
        }
        KieContainer kContainer = kieServices.newKieContainer(kieServices
                .getRepository().getDefaultReleaseId());

        assertNotNull(kContainer.getKieBase("SimpleRuleKBase"));
        KieSession kSession = kContainer.newKieSession("simpleRuleKSession");

        kSession.fireAllRules();
    }


    @Test
    public void 函数定义与查询() {
        KieSession kSession = kieContainer.newKieSession("simpleRuleKSession");
        Member member = new Member();
        member.setIdentity("copper");
        member.setAmount(101);

        kSession.insert(member);
        AgendaFilter agendaFilter = match -> StringUtils.contains(match.getRule().getName(), "function");
        kSession.fireAllRules(agendaFilter);

        kSession.setGlobal("param", "param value");
        kSession.setGlobal("me", member);
    }

    @Test
    public void 规则激活组() {
        KieSession kSession = kieContainer.newKieSession("simpleRuleKSession");
        Member member = new Member();
        member.setIdentity("copper");
        member.setAmount(110);

        kSession.insert(member);
        AgendaFilter agendaFilter = match -> {
            Rule rule = match.getRule();
            return rule.getName().startsWith("acivation group");
        };
        kSession.fireAllRules(agendaFilter);
        kSession.dispose();

    }

    @Test
    public void 操作规则事实对象() {
        KieSession kSession = kieContainer.newKieSession("simpleRuleKSession");
        Member member = new Member();
        member.setIdentity("copper");
        member.setAmount(150);

        kSession.insert(member);
        AgendaFilter agendaFilter = match -> {
            Rule rule = match.getRule();
            return rule.getName().startsWith("discount a");
        };
        kSession.fireAllRules(agendaFilter);
        System.out.println(member);
        kSession.dispose();
    }

    @Test
    public void 整合测试一() {
        KieSession kSession = kieContainer.newKieSession("simpleRuleKSession");

        Member member = new Member();
        member.setIdentity("gold");
        member.setAmount(102);

        kSession.insert(member);
        AgendaFilter agendaFilter = match -> {
            Rule rule = match.getRule();
            return rule.getName().startsWith("withDrl");
        };
        kSession.fireAllRules(agendaFilter);
        System.out.println(member);
        kSession.dispose();
    }

    /**
     * 整合失败缺失版本例子
     */
    @Test
    public void 整合测试二() {
        ProcessInstance processInstance = ActivitiFactory.deployAndStart("processes/_9Activiti整合Drools.bpmn20.xml", "drools/_95整合.drl");

        // 完成第一个任务并设置销售参数
        // 设置参数
        Map<String, Object> vars = new HashMap<String, Object>();
        Member m = new Member();
        m.setIdentity("gold");
        m.setAmount(100);
        vars.put("member", m);
        ActivitiFactory.complete(processInstance,vars);
    }
}
