package cn.adbyte.activiti.feign.api;

import cn.adbyte.activiti.feign.api.entity.ProcessDefinitionsEntityCustom;
import feign.*;

import java.util.Map;

/**
 * REST 流程定义服务
 *
 * @author Adam
 * @see <a href='https://www.activiti.org/userguide/#_process_definitions'></a>
 */
public interface ProcessDefinitionsApi {

    @RequestLine("GET repository/process-definitions")
    Results<ProcessDefinitionsEntityCustom> listProcessDefinitions(@QueryMap Map<String, Object> map);

    @RequestLine("GET repository/process-definitions/{processDefinitionId}")
    ProcessDefinitionsEntityCustom getProcessDefinition(@Param("processDefinitionId") String processDefinitionId);

    /** json curly braces must be escaped!*/

    @RequestLine("PUT repository/process-definitions/{processDefinitionId}")
    @Headers("Content-Type: application/json")
    @Body("%7B\"category\": \"{category}\"%7D")
    Integer updateCategory(@Param("processDefinitionId") String processDefinitionId, @Param("category") String category);
    /**
     * 更多内容不想再继续调用下去了，
     * 太多了。。。看API吧！
     * 以常用的添加到Feign服务里可以日常测试即可
     */
}
