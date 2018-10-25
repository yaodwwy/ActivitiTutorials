package cn.adbyte.activiti.feign.test;

import lombok.Data;

@Data
public class FeignAuthResult {
    private boolean authenticated;
    private String user;
}
