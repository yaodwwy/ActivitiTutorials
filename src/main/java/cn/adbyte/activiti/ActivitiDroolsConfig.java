package cn.adbyte.activiti;

import org.activiti.engine.*;
import org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Adam
 */
@Configuration
@ComponentScan
public class ActivitiDroolsConfig {

    @Bean
    public ProcessEngine processEngine() {

        /**
         * 数据源配置
         */
        ProcessEngineConfiguration cfg = new StandaloneProcessEngineConfiguration()
                .setJdbcUrl("jdbc:mysql://localhost:3306/ACT?characterEncoding=UTF-8")
                .setJdbcUsername("root")
                .setJdbcPassword("root")
                .setJdbcDriver("com.mysql.jdbc.Driver")
                .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE)
                .setAsyncExecutorActivate(true)
                .setCreateDiagramOnDeploy(true);


        ProcessEngine processEngine = cfg.buildProcessEngine();
        String pName = processEngine.getName();
        String ver = ProcessEngine.VERSION;
        System.out.println("ProcessEngine [" + pName + "] Version: [" + ver + "]");

        return processEngine;
    }

    @Bean
    public RepositoryService repositoryService() {
        // 存储服务
        return processEngine().getRepositoryService();
    }

    @Bean
    public RuntimeService runtimeService() {
        // 运行时服务
        return processEngine().getRuntimeService();
    }

    @Bean
    public TaskService taskService() {
        // 任务服务
        return processEngine().getTaskService();
    }

    @Bean
    public IdentityService identityService() {
        // 用户身份服务
        return processEngine().getIdentityService();
    }

    @Bean
    public ManagementService managementService() {
        // 管理服务
        return processEngine().getManagementService();
    }

}
