package cn.adbyte.activiti.delegate;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * @author Adam
 * 如果是通过delegateExpression 委托表达式${} 声明的，必须要实现序列化
 */
public class EventDelegate implements JavaDelegate , Serializable {

    private static final Logger logger = LoggerFactory.getLogger(EventDelegate.class);

    @Override
    public void execute(DelegateExecution arg0) {
        logger.debug("正常处理事件委托");
        if(arg0.getVariables().size()>0){
            logger.debug(arg0.getVariables().toString());
        }
    }

}
