package cn.adbyte.activiti.mvel;

public class MyMethod {

    public static String testMethod(String name, Integer age) {
        System.out.println("#############");
        return "名称：" + name + ", 年龄：" + age;
    }
}
