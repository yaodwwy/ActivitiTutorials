package cn.adbyte.activiti;

import org.activiti.engine.RuntimeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@Import({ActivitiConfig.class})
public class _6BPMN事件Test {

    @Autowired
    private ActivitiFactory ActivitiFactory;

    @Autowired
    private RuntimeService runService;

    @Test
    public void 中间抛出事件补偿() {
        ActivitiFactory.deployAndStart("processes/_6中间抛出事件补偿.bpmn20.xml");
    }
}
