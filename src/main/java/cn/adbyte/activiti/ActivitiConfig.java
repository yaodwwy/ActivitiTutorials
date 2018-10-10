package cn.adbyte.activiti;

import org.activiti.engine.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Adam
 */
@Configuration
public class ActivitiConfig {

    @Bean
    public ProcessEngine processEngine() {

        /**
         * 数据源配置
         */
        ProcessEngineConfiguration config = ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/ACT?characterEncoding=UTF-8");
        config.setJdbcDriver("com.mysql.jdbc.Driver");
        config.setJdbcUsername("root");
        config.setJdbcPassword("root");
        config.setDatabaseSchemaUpdate("true");
        config.setAsyncExecutorActivate(true);
        return config.buildProcessEngine();
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
    public ManagementService managementService(){
        // 管理服务
        return processEngine().getManagementService();
    }

}
