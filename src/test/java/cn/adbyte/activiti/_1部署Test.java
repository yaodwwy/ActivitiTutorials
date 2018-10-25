package cn.adbyte.activiti;

import org.activiti.bpmn.BpmnAutoLayout;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.List;
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
 *
 * 流程定义的删除行为：
 * 1. 不管是否指定级联，都会删除部署相关的身份数据、流程定义数据、流程资源与部署数据。
 * 2. 如果设置为级联删除，则会将运行的流程实例、流程任务以及流程实例的历史数据删除。
 * 3. 如果不级联删除，但是存在运行时数据，例如还有流程实例，就会删除失败。
 */
@RunWith(SpringRunner.class)
@Import({ActivitiConfig.class})
public class _1部署Test {

    @Autowired
    private ActivitiFactory ActivitiFactory;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runService;

    private DeploymentBuilder builder;

    @PostConstruct
    private void init() {
        builder = repositoryService.createDeployment();
    }

    @Test
    public void 构建BPMN模型() throws Exception {
        // 创建BPMN模型对象
        BpmnModel bpmnModel = new BpmnModel();
        // 创建一个流程定义
        Process process = new Process();
        bpmnModel.addProcess(process);
        process.setId("firstProcess"); //processKey
        process.setName("My firstProcess");

        // 创建Flow元素（所有的事件、任务都被认为是Flow）
        process.addFlowElement(createStartEvent());
        process.addFlowElement(createUserTask("task1", "First task", "张三"));
        process.addFlowElement(createEndEvent());
        process.addFlowElement(createSequenceFlow("start", "task1"));
        process.addFlowElement(createSequenceFlow("task1", "end"));

        //自动布局
        new BpmnAutoLayout(bpmnModel).execute();
        ActivitiFactory.deploy("first", bpmnModel,"firstProcess", true);
    }

    @Test
    public void 生成PNG() {
        ActivitiFactory.generatePng("scope.bpmn20.xml");
    }


    @Test
    public void 部署文本文件() throws IOException {
        builder.addClasspathResource("deploy/my_text.txt");
        Deployment deploy = builder.deploy();
        // 数据查询
        InputStream inputStream = repositoryService.getResourceAsStream(deploy.getId(), "deploy/my_text.txt");
        int count = inputStream.available();
        byte[] contents = new byte[count];
        inputStream.read(contents);
        String result = new String(contents);
        //输入结果
        System.out.println("部署文本文件: " + result);
    }

    /**
     * 自动解压部署压缩包里的文件
     * @throws FileNotFoundException
     */
    @Test
    public void 部署Zip文件() throws FileNotFoundException {
        FileInputStream fis = new FileInputStream(new File("src/main/resources/deploy/datas.zip"));
        ZipInputStream zis = new ZipInputStream(fis);
        builder = repositoryService.createDeployment();
        builder.addZipInputStream(zis);
        Deployment deploy = builder.deploy();
        System.out.println("部署Zip文件完成，可在数据库中查看" + deploy.getId() + ", " + deploy.getName());
    }

    @Test
    public void 暂停挂起流程定义(){
        ProcessInstance processInstance = ActivitiFactory.deployAndStart("processes/_1first.bpmn20.xml");
        String processDefinitionID = processInstance.getProcessDefinitionId();
        repositoryService.suspendProcessDefinitionById(processDefinitionID);
        try {
            runService.startProcessInstanceById(processDefinitionID);
        } catch (ActivitiException e) {
            String message = e.getMessage();
            System.out.println(message);
            boolean suspended = message.endsWith("suspended");
            Assert.assertEquals(suspended,true);
        }
    }

    @Test
    public void 动态部署() throws Exception {
        // 1. 创建一个空的BpmnModel和Process对象
        BpmnModel model = new BpmnModel();
        Process process = new Process();
        model.addProcess(process);
        process.setId("my-process");

        // 创建Flow元素（所有的事件、任务都被认为是Flow）
        process.addFlowElement(createStartEvent());
        process.addFlowElement(createUserTask("task1", "First task", "fred"));
        process.addFlowElement(createUserTask("task2", "Second task", "john"));
        process.addFlowElement(createEndEvent());

        process.addFlowElement(createSequenceFlow("start", "task1"));
        process.addFlowElement(createSequenceFlow("task1", "task2"));
        process.addFlowElement(createSequenceFlow("task2", "end"));

        // 2. 流程图自动布局（位于activiti-bpmn-layout模块）
        new BpmnAutoLayout(model).execute();

        // 3. 把BpmnModel对象部署到引擎
        Deployment deployment = repositoryService.createDeployment()
                .addBpmnModel("dynamic-model.bpmn", model).name("Dynamic process deployment")
                .deploy();

        // 4. 启动流程
        ProcessInstance processInstance = runService
                .startProcessInstanceByKey("my-process");

        // 5. 检查流程是否正常启动
        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId()).list();

        Assert.assertEquals(1, tasks.size());
        Assert.assertEquals("First task", tasks.get(0).getName());
        Assert.assertEquals("fred", tasks.get(0).getAssignee());

        // 6. 导出流程图
        InputStream processDiagram = repositoryService
                .getProcessDiagram(processInstance.getProcessDefinitionId());

        // 把文件生成在本章项目的generated目录中
        String userHomeDir = "src/main/resources/generated";
        FileUtils.copyInputStreamToFile(processDiagram, new File(userHomeDir + "/diagram.png"));

        // 7. 导出Bpmn文件到本地文件系统
        InputStream processBpmn = repositoryService
                .getResourceAsStream(deployment.getId(), "dynamic-model.bpmn");
        FileUtils.copyInputStreamToFile(processBpmn,
                new File(userHomeDir + "/process.bpmn20.xml"));
    }
    // 用户任务
    protected UserTask createUserTask(String id, String name, String assignee) {
        UserTask userTask = new UserTask();
        userTask.setName(name);
        userTask.setId(id);
        userTask.setAssignee(assignee);
        return userTask;
    }
    // 添加流程顺序
    protected SequenceFlow createSequenceFlow(String from, String to) {
        SequenceFlow flow = new SequenceFlow();
        flow.setSourceRef(from);
        flow.setTargetRef(to);
        return flow;
    }
    // 开始事件
    protected StartEvent createStartEvent() {
        StartEvent startEvent = new StartEvent();
        startEvent.setId("start");
        return startEvent;
    }
    // 结束事件
    protected EndEvent createEndEvent() {
        EndEvent endEvent = new EndEvent();
        endEvent.setId("end");
        return endEvent;
    }
}
