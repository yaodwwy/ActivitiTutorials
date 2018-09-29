package cn.adbyte.activiti.$2_query_deploy_processDefinition;

import org.activiti.bpmn.model.*;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.identity.User;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
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
public class DeploymentTest {
    public static void main(String[] args) throws Exception {
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        // 存储服务
        RepositoryService rs = engine.getRepositoryService();
        DeploymentBuilder builder = rs.createDeployment();

        //添加BPMN模型
        builder.addBpmnModel("My Process", createProcessModel());
        builder.deploy();
        System.out.println("添加BPMN模型 完成");

        //部署验证
        String 流程错误 = "bpmn_error.bpmn";
        String 语法错误 = "schema_error.bpmn";
        builder.addClasspathResource(流程错误);
        builder.addClasspathResource(语法错误);
        builder.disableSchemaValidation(); //关闭语法错误检查
        builder.disableBpmnValidation(); //关闭流程错误验证
        try {
            builder.deploy();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // 查询部署资源
        // 部署一份流程文件
        Deployment dep = rs.createDeployment().addClasspathResource("gen.bpmn").deploy();
        // 查询流程定义
        //查询流程定义实体
        ProcessDefinition def = rs.createProcessDefinitionQuery().deploymentId(dep.getId()).singleResult();
        // 查询资源文件
        InputStream inputStream = rs.getProcessModel(def.getId());
        // 读取输入流
        int count = inputStream.available();
        byte[] contents = new byte[count];
        inputStream.read(contents);
        String result = new String(contents);
        //输入输出结果
        File file = new File("gen.bpmn20.xml");
        if (!file.exists()) {
            file.createNewFile();
        }
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(result);
        fileWriter.close();
        System.out.println(file.getAbsolutePath());
        System.out.println("xml 写入完成！");

        // 生成流程图文件
        // 查询资源文件
        inputStream = rs.getProcessDiagram(def.getId());
        // 将输入流转换为图片对象
        BufferedImage image = ImageIO.read(inputStream);
        // 保存为图片文件
        File png = new File("gen.png");
        if (!png.exists()) {
            png.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream(png);
        ImageIO.write(image, "png", fos);
        fos.close();
        inputStream.close();

        //部署时附带图片
        builder = rs.createDeployment();
        builder.addClasspathResource("gen.bpmn").addClasspathResource("gen.png");
        builder.deploy();
        System.out.println("部署时附带图片完成 在资源表里查看");

        //定义流程发起人，候选人
        IdentityService is = engine.getIdentityService();
        User user = is.newUser(UUID.randomUUID().toString());
        user.setFirstName("Angus");
        is.saveUser(user);

        builder = rs.createDeployment();
        builder.addClasspathResource("first.bpmn");
        dep = builder.deploy();


        def = rs.createProcessDefinitionQuery()
                .deploymentId(dep.getId()).singleResult();
        //候选流程发起人
        rs.addCandidateStarterUser(def.getId(), user.getId());

        List<ProcessDefinition> defs = rs.createProcessDefinitionQuery().startableByUser(user.getId()).list();
        for(ProcessDefinition de : defs) {
            System.out.println("ProcessDefinition 流程定义：" + de.getId() + "， " + de.getName());
        }

        // 部署文本文件
        builder = rs.createDeployment();
        builder.addClasspathResource("my_text.txt");
        dep = builder.deploy();
        // 数据查询
        inputStream = rs.getResourceAsStream(dep.getId(), "my_text.txt");
        count = inputStream.available();
        contents = new byte[count];
        inputStream.read(contents);
        result = new String(contents);
        //输入结果
        System.out.println("部署文本文件: "+result);

        // 部署Zip文件
        FileInputStream fis = new FileInputStream(new File("first/src/main/resources/datas.zip"));
        ZipInputStream zis = new ZipInputStream(fis);
        builder = rs.createDeployment();
        builder.addZipInputStream(zis);
        Deployment deploy = builder.deploy();
        System.out.println("部署Zip文件完成，可在数据库中查看" + deploy.getId() + ", " + deploy.getName());

    }

    /**
     * 代码构建BPMN模型
     */
    private static BpmnModel createProcessModel() {
        // 创建BPMN模型对象
        BpmnModel model = new BpmnModel();
        // 创建一个流程定义
        org.activiti.bpmn.model.Process process = new org.activiti.bpmn.model.Process();
        model.addProcess(process);
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
        return model;
    }

    /*
    流程定义的删除行为：
    1. 不管是否指定级联，都会删除部署相关的身份数据、流程定义数据、流程资源与部署数据。
    2. 如果设置为级联删除，则会将运行的流程实例、流程任务以及流程实例的历史数据删除。
    3. 如果不级联删除，但是存在运行时数据，例如还有流程实例，就会删除失败。
    * */
}
