package cn.adbyte.activiti;

import org.activiti.bpmn.BpmnAutoLayout;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;


@Component
public class ActivitiFactory {
    private static final Logger logger = LoggerFactory.getLogger(ActivitiFactory.class);
    /**
     * 存储服务
     */
    @Autowired
    private RepositoryService repositoryService;
    /**
     * 运行时服务
     */
    @Autowired
    private RuntimeService runService;
    /**
     * 任务服务
     */
    @Autowired
    private TaskService taskService;
    /**
     * 管理服务
     */
    @Autowired
    private ManagementService managementService;


    /**
     * 部署并启动一个流程
     *
     * @param bpmn20Xml
     * @return
     */
    public ProcessInstance deployAndStart(String bpmn20Xml) {
        if (bpmn20Xml == null) {
            throw new RuntimeException("流程文件不能为空！");
        }
        DeploymentBuilder builder = repositoryService.createDeployment();
        builder.addClasspathResource(bpmn20Xml);
        // 关闭语法错误检查 ( DTD格式检查 )
        // builder.disableSchemaValidation();
        // 关闭流程错误验证 ( 流程图画的不对 如 流程冲突 )
        // builder.disableBpmnValidation();
        // 部署
        Deployment dep = builder.deploy();
        logger.debug("部署：" + bpmn20Xml);
        ProcessDefinition pd = repositoryService.createProcessDefinitionQuery().deploymentId(dep.getId()).singleResult();
        // 启动流程
        ProcessInstance processInstance = runService.startProcessInstanceById(pd.getId(), bpmn20Xml);
        logger.debug("流程实例id：" + processInstance.getId() + ", name:" + processInstance.getName() + "已启动 ...");
        return processInstance;
    }

    public void complete(ProcessInstance pi) {
        if (pi == null) {
            throw new RuntimeException("流程实例不能为空！");
        }

        logger.debug("当前流程实例id：" + pi.getId() + ", name:" + pi.getName() + "正在获取任务 ...");

        List<Task> tasks = taskService.createTaskQuery().processInstanceId(pi.getId()).list();

        Print.tasks(tasks);
        for (Task task : tasks) {
            logger.debug("当前任务：" + task.getName());
            taskService.complete(task.getId());
            logger.debug("任务已完成");
        }

    }


    final String GEN_PATH = "src/main/resources/generated/";

    /**
     * 构建BPMN模型
     *
     * @param fileName  不需要扩展名
     * @param bpmnModel
     * @param png
     * @throws IOException
     */
    public Deployment deploy(String fileName, BpmnModel bpmnModel, boolean png) throws Exception {
        //获取绘图生成器
        DefaultProcessDiagramGenerator generator = new DefaultProcessDiagramGenerator();
        InputStream pngStream = generator.generatePngDiagram(bpmnModel);

        DeploymentBuilder builder = repositoryService.createDeployment();
        //act_ge_bytearray 表中的NAME_
        String bpmnFileName = fileName + ".bpmn20.xml";
        String pngFileName = fileName + ".png";
        builder.addBpmnModel(bpmnFileName, bpmnModel).addInputStream(pngFileName, pngStream);
        Deployment deploy = builder.deploy();
        String deployID = deploy.getId();
        InputStream deploymentResource = repositoryService.getResourceAsStream(deployID, bpmnFileName);
        // 读取输入流
        int count = deploymentResource.available();
        byte[] contents = new byte[count];
        deploymentResource.read(contents);
        String result = new String(contents);
        //输入输出结果
        File xmlFile = new File(GEN_PATH + bpmnFileName);
        if (!xmlFile.exists()) {
            xmlFile.createNewFile();
        }
        FileWriter fileWriter = new FileWriter(xmlFile);
        fileWriter.write(result);
        fileWriter.close();
        logger.debug("流程文件" + xmlFile.getAbsolutePath() + " 写入完成！");
        deploymentResource.close();
        if(png){
            // 将输入流转换为图片对象
            // 保存为图片文件
            File file = new File(GEN_PATH + fileName + ".png");
            if (!file.exists()) {
                file.createNewFile();
            }
            InputStream pngResource = repositoryService.getResourceAsStream(deployID, pngFileName);
            BufferedImage bufferedImage = ImageIO.read(pngResource);
            FileOutputStream fos = new FileOutputStream(file);
            ImageIO.write(bufferedImage, "png", fos);
            fos.close();
            logger.debug("流程图片" + file.getAbsolutePath() + " 写入完成！");
        }
        return deploy;
    }

}
