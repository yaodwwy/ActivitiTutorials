package cn.adbyte.activiti.feign.test;

import feign.RequestLine;

public interface FeignFirst {

    @RequestLine("GET /ip")
    Ip getIp();
}
