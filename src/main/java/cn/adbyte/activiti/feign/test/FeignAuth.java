package cn.adbyte.activiti.feign.test;

import feign.RequestLine;

public interface FeignAuth {

    @RequestLine("GET /basic-auth/user/passwd")
    FeignAuthResult getAuthResult();
}