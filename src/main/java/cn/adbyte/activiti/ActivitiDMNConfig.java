package cn.adbyte.activiti;

import org.activiti.dmn.api.DmnRepositoryService;
import org.activiti.dmn.api.DmnRuleService;
import org.activiti.dmn.engine.DmnEngine;
import org.activiti.dmn.engine.DmnEngineConfiguration;
import org.activiti.engine.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Adam
 */
@Configuration
@ComponentScan
public class ActivitiDMNConfig {

    @Bean
    public DmnEngine dmnEngine() {
        /**
         * 数据源配置
         */
        DmnEngineConfiguration config = DmnEngineConfiguration.createStandaloneDmnEngineConfiguration();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/ACT?characterEncoding=UTF-8");
        config.setJdbcDriver("com.mysql.jdbc.Driver");
        config.setJdbcUsername("root");
        config.setJdbcPassword("root");
        config.setDatabaseSchemaUpdate("true");
        return config.buildDmnEngine();
    }

    @Bean
    public DmnRepositoryService dmnRepositoryService() {
        // 存储服务
        return dmnEngine().getDmnRepositoryService();
    }

    @Bean
    public DmnRuleService dmnRuleService() {
        // 获取规则服务组件
        return dmnEngine().getDmnRuleService();
    }


}
