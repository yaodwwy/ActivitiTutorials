package cn.adbyte.activiti.pojo;

import lombok.Data;

@Data
public class Message {
    public static final String HELLO = "hello";
    public static final String GOODBYE = "goodbye";
    private String message;
    private String status;
}
