package cn.adbyte.activiti.$5_开始事件_结束事件_边界事件;

import cn.adbyte.activiti.ActivitiConfig;
import cn.adbyte.activiti.ActivitiFactory;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Adam
 */
@RunWith(SpringRunner.class)
@Import({ActivitiConfig.class})
public class 中间事件测试 {

    @Autowired
    private ActivitiFactory ActivitiFactory;

    @Test
    public void 中间定时器捕获事件() throws Exception {
        ProcessInstance pi = ActivitiFactory.deployAndStart("中间定时器捕获事件.bpmn");
        ActivitiFactory.complete(pi);
        Thread.sleep(10_000);
        ActivitiFactory.complete(pi);
    }

    @Test
    public void 中间信号捕获事件() {
        ProcessInstance pi = ActivitiFactory.deployAndStart("中间定时器捕获事件.bpmn");
        ActivitiFactory.complete(pi);
    }

}
