package cn.adbyte.activiti;

import org.activiti.bpmn.BpmnAutoLayout;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.*;
import org.activiti.engine.identity.User;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipInputStream;

/**
 * 流程文件部署
 * addClasspathResource
 * addInputStream
 * addString
 * addZipInputStream
 * addBpmnModel
 * addBytes
 * deploy
 */
@RunWith(SpringRunner.class)
@Import({ActivitiConfig.class})
public class _1部署Test {

    @Autowired
    private ActivitiFactory ActivitiFactory;
    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runService;

    @Test
    public void 构建BPMN模型() throws Exception {
        // 创建BPMN模型对象
        BpmnModel bpmnModel = new BpmnModel();
        // 创建一个流程定义
        Process process = new Process();
        bpmnModel.addProcess(process);
        process.setId("myProcess");
        process.setName("My Process");
        // 开始事件
        StartEvent startEvent = new StartEvent();
        startEvent.setId("startEvent");
        process.addFlowElement(startEvent);
        // 用户任务
        UserTask userTask = new UserTask();
        userTask.setName("User Task");
        userTask.setId("userTask");
        process.addFlowElement(userTask);
        // 结束事件
        EndEvent endEvent = new EndEvent();
        endEvent.setId("endEvent");
        process.addFlowElement(endEvent);
        // 添加流程顺序
        process.addFlowElement(new SequenceFlow("startEvent", "userTask"));
        process.addFlowElement(new SequenceFlow("userTask", "endEvent"));

        //自动布局
        new BpmnAutoLayout(bpmnModel).execute();

        Deployment deploy = ActivitiFactory.deploy("构建自定义的BPMN模型new", bpmnModel, true);
    }

    public void main(String[] args) throws Exception {
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        // 存储服务
        RepositoryService rs = engine.getRepositoryService();
        DeploymentBuilder builder = rs.createDeployment();


        //定义流程发起人，候选人
        IdentityService is = engine.getIdentityService();
        User user = is.newUser(UUID.randomUUID().toString());
        user.setFirstName("Angus");
        is.saveUser(user);

        builder = rs.createDeployment();
        builder.addClasspathResource("first.bpmn");
        Deployment dep = builder.deploy();

        ProcessDefinition pd = repositoryService.createProcessDefinitionQuery().deploymentId(dep.getId()).singleResult();
        pd = rs.createProcessDefinitionQuery()
                .deploymentId(dep.getId()).singleResult();
        //候选流程发起人
        rs.addCandidateStarterUser(pd.getId(), user.getId());

        List<ProcessDefinition> defs = rs.createProcessDefinitionQuery().startableByUser(user.getId()).list();
        for (ProcessDefinition de : defs) {
            System.out.println("ProcessDefinition 流程定义：" + de.getId() + "， " + de.getName());
        }

        // 部署文本文件
        builder = rs.createDeployment();
        builder.addClasspathResource("other/my_text.txt");
        dep = builder.deploy();
        // 数据查询
        InputStream inputStream = rs.getResourceAsStream(dep.getId(), "other/my_text.txt");
        int count = inputStream.available();
        byte[]contents = new byte[count];
        inputStream.read(contents);
        String result = new String(contents);
        //输入结果
        System.out.println("部署文本文件: " + result);

        // 部署Zip文件
        FileInputStream fis = new FileInputStream(new File("other/datas.zip"));
        ZipInputStream zis = new ZipInputStream(fis);
        builder = rs.createDeployment();
        builder.addZipInputStream(zis);
        Deployment deploy = builder.deploy();
        System.out.println("部署Zip文件完成，可在数据库中查看" + deploy.getId() + ", " + deploy.getName());

    }

    /*
    流程定义的删除行为：
    1. 不管是否指定级联，都会删除部署相关的身份数据、流程定义数据、流程资源与部署数据。
    2. 如果设置为级联删除，则会将运行的流程实例、流程任务以及流程实例的历史数据删除。
    3. 如果不级联删除，但是存在运行时数据，例如还有流程实例，就会删除失败。
    * */
}
