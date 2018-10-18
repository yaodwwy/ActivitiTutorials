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
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


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
    public ProcessInstance deployAndStart(Map<String, Object> param, String... bpmn20Xml) {
        if (bpmn20Xml == null) {
            throw new RuntimeException("流程文件不能为空！");
        }
        DeploymentBuilder builder = repositoryService.createDeployment();
        for (String bpmn : bpmn20Xml) {
            builder.addClasspathResource(bpmn);
            builder.name(bpmn);
        }
        // 关闭语法错误检查 ( DTD格式检查 )
        // builder.disableSchemaValidation();
        // 关闭流程错误验证 ( 流程图画的不对 如 流程冲突 )
        // builder.disableBpmnValidation();
        // 部署
        Deployment dep = builder.deploy();
        logger.debug("部署：");
        for (String bpmn : bpmn20Xml) {
            logger.debug(bpmn);
        }
        List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery().deploymentId(dep.getId()).list();
        // 启动流程
        ProcessInstance processInstance = null;
        for (ProcessDefinition p : processDefinitions) {
            processInstance = runService.startProcessInstanceById(p.getId(), p.getKey(), param);
            logger.debug("流程实例id：" + processInstance.getId() + ", BusinessKey:" + processInstance.getBusinessKey() + " 已启动 ...");
        }
        logger.debug("这里默认只返回一个流程实例###");
        return processInstance;
    }

    public ProcessInstance deployAndStart(String... bpmn20Xml) {
        return this.deployAndStart(null, bpmn20Xml);
    }

    public void complete(ProcessInstance pi) {
        complete(pi, null);
    }

    public void complete(ProcessInstance pi, Map<String, Object> params) {
        if (pi == null) {
            throw new RuntimeException("流程实例不能为空！");
        }

        logger.debug("当前流程实例id：" + pi.getId() + ", BusinessKey:" + pi.getBusinessKey() + "正在获取任务 ...");

        List<Task> tasks = taskService.createTaskQuery().processInstanceId(pi.getId()).list();

        Print.tasks(tasks);
        for (Task task : tasks) {
            logger.debug("当前任务：" + task.getName());
            taskService.complete(task.getId(), params);
            logger.debug("任务已完成");
            List<Task> tasks2 = taskService.createTaskQuery().processInstanceId(pi.getId()).list();
            for (Task t : tasks2) {
                logger.debug("当前最新任务：" + t.getName());
            }
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
    public String deploy(String fileName, BpmnModel bpmnModel, String processKey, boolean png) throws Exception {

        //act_ge_bytearray 表中的NAME_
        String bpmnFileName = fileName + ".bpmn20.xml";
        String pngFileName = fileName + ".png";

        // 把BpmnModel对象部署到引擎
        DeploymentBuilder builder = repositoryService.createDeployment();
        Deployment deployment = builder.addBpmnModel(bpmnFileName, bpmnModel).name("Dynamic process deployment")
                .deploy();

        // 启动流程
        ProcessInstance processInstance = runService.startProcessInstanceByKey(processKey);

        // 检查流程是否正常启动
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
        Assert.assertEquals(true, tasks.size() > 0);

        String deployID = deployment.getId();
        InputStream deploymentResource = repositoryService.getResourceAsStream(deployID, bpmnFileName);


        // 把文件生成在本章项目的generated目录中
        String userHomeDir = "src/main/resources/generated/";

        // 导出Bpmn20.xml文件到本地文件系统
        InputStream processBpmn = repositoryService.getResourceAsStream(deployment.getId(), bpmnFileName);
        File xmlFile = new File(userHomeDir + bpmnFileName);
        FileUtils.copyInputStreamToFile(processBpmn, xmlFile);

        logger.debug("流程文件" + xmlFile.getAbsolutePath() + " 写入完成！");
        deploymentResource.close();
        if (png) {
            // 导出流程图
            InputStream processDiagram = repositoryService.getProcessDiagram(processInstance.getProcessDefinitionId());
            File pngFile = new File(userHomeDir + pngFileName);
            FileUtils.copyInputStreamToFile(processDiagram, pngFile);
            logger.debug("流程图片" + pngFile.getAbsolutePath() + " 写入完成！");
            processDiagram.close();
        }
        return deployID;
    }

    private void writePng(String fileName, InputStream pngResource) {
        BufferedImage bufferedImage = null;
        try {
            File file = new File(GEN_PATH + fileName + ".png");
            if (!file.exists()) {
                file.createNewFile();
            }
            bufferedImage = ImageIO.read(pngResource);
            FileOutputStream fos = new FileOutputStream(file);
            ImageIO.write(bufferedImage, "png", fos);
            fos.close();
            logger.debug("流程图片" + file.getAbsolutePath() + " 写入完成！");
        } catch (IOException e) {
            logger.error("流程图片写入失败！" + e.getMessage());
        }
    }

    /**
     * 快速由xml文件生成png
     *
     * @param deployFileName
     */
    public void generatePng(String deployFileName) {
        Deployment dep = repositoryService.createDeployment().addClasspathResource(deployFileName).deploy();
        ProcessDefinition pd = repositoryService.createProcessDefinitionQuery().deploymentId(dep.getId()).singleResult();
        InputStream processDiagram = repositoryService.getProcessDiagram(pd.getId());
        writePng(deployFileName, processDiagram);
    }
}
