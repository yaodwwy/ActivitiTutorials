package cn.adbyte.activiti.mvel;

import org.activiti.dmn.engine.CustomExpressionFunctionRegistry;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.util.ClassUtils.getMethod;

public class CustomRegistry implements CustomExpressionFunctionRegistry {

    // 用于存放方法的集合，key为自定义的方法名，value为方法实例
    protected static Map<String, Method> customFunctionConfigurations = new HashMap<String, Method>();

    static {
        // 将方法实例添加到集合中
        Method m = getMethod(MyMethod.class, "testMethod", String.class,
                Integer.class);
        customFunctionConfigurations.put("fn_testMethod", m);

//        Method saleM = getMethod(Sale.class, "doDiscount", Double.class);
//        customFunctionConfigurations.put("fn_doDiscount", saleM);
    }

    @Override
    public Map<String, Method> getCustomExpressionMethods() {

        return customFunctionConfigurations;
    }

}
