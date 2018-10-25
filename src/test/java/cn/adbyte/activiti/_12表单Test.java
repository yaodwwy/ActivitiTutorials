package cn.adbyte.activiti;

import org.activiti.engine.*;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.history.HistoricDetail;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(SpringRunner.class)
@Import({ActivitiConfig.class})
public class _12表单Test {

    @Autowired
    private ActivitiFactory activitiFactory;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private FormService formService;

    @Autowired
    private HistoryService historyService;


    @Test
    public void 动态表单() throws FileNotFoundException {

        List<ProcessDefinition> processDefinitions = activitiFactory.deploy("processes/_12表单.bpmn20.xml");
        ProcessDefinition processDefinition = processDefinitions.get(0);
        StartFormData startFormData = formService.getStartFormData(processDefinition.getId());
        assertNull(startFormData.getFormKey());

        Map<String, String> formProperties = new HashMap<String, String>();
        formProperties.put("name", "HenryYan");
        /**
         * 流程实例是通过提交表单来启动
         */
        ProcessInstance processInstance = formService.submitStartFormData(processDefinition.getId(), formProperties);
        assertNotNull(processInstance);

        // 运行时变量
        Map<String, Object> variables = runtimeService.getVariables(processInstance.getId());
        assertEquals(variables.size(), 1);
        Set<Map.Entry<String, Object>> entrySet = variables.entrySet();
        for (Map.Entry<String, Object> entry : entrySet) {
            System.out.println(entry.getKey() + "=" + entry.getValue());
        }

        // 历史记录
        List<HistoricDetail> list = historyService.createHistoricDetailQuery().formProperties().processInstanceId(processInstance.getId()).list();
        assertEquals(1, list.size());

        // 获取第一个任务节点
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        assertEquals("First Step", task.getName());

        TaskFormData taskFormData = formService.getTaskFormData(task.getId());
        assertNotNull(taskFormData);
        assertNull(taskFormData.getFormKey());
        List<FormProperty> taskFormProperties = taskFormData.getFormProperties();
        assertNotNull(taskFormProperties);
        for (FormProperty formProperty : taskFormProperties) {
            System.out.println(ToStringBuilder.reflectionToString(formProperty));
        }
        formProperties = new HashMap<String, String>();
        formProperties.put("setInFirstStep", "01/12/2012");
        /**
         * 提交表单并完成任务
         */
        formService.submitTaskFormData(task.getId(), formProperties);

        // 获取第二个节点
        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).taskName("Second Step").singleResult();
        assertNotNull(task);
        taskFormData = formService.getTaskFormData(task.getId());
        assertNotNull(taskFormData);
        List<FormProperty> formProperties2 = taskFormData.getFormProperties();
        for (FormProperty formProperty : formProperties2) {
            System.out.println(ToStringBuilder.reflectionToString(formProperty));
        }
        assertNotNull(formProperties2);
        assertEquals(1, formProperties2.size());
        assertNotNull(formProperties2.get(0).getValue());
        assertEquals(formProperties2.get(0).getValue(), "01/12/2012");
    }
}
