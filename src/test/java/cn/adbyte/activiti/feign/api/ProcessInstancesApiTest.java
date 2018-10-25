package cn.adbyte.activiti.feign.api;

import cn.adbyte.activiti.FeignConfig;
import cn.adbyte.activiti.feign.api.entity.ProcessInstanceEntityCustom;
import cn.adbyte.activiti.feign.api.param.StartProcessInstanceParam;
import cn.adbyte.activiti.feign.decoder.StatusDecoder;
import com.fasterxml.jackson.core.io.JsonStringEncoder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonOrgJsonProvider;
import feign.Feign;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.jackson.JacksonDecoder;
import org.apache.commons.codec.StringEncoder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;

@RunWith(SpringRunner.class)
@Import({FeignConfig.class})
public class ProcessInstancesApiTest {

    private final String url = "http://localhost:8080/activiti-rest/service";

    @Autowired
    private Feign.Builder builder;

    private ProcessInstancesApi processInstances;
    private ProcessInstancesApi statusProcessInstances;

    @PostConstruct
    private void init(){
        GsonDecoder decoder = new GsonDecoder();
        processInstances = builder.decoder(decoder).target(ProcessInstancesApi.class, url);
        StatusDecoder statusDecoder = new StatusDecoder();
        statusProcessInstances = builder.decoder(statusDecoder).target(ProcessInstancesApi.class, url);
    }

    @Test
    public void startProcessInstance() {
        StartProcessInstanceParam.ById id = StartProcessInstanceParam.createId("审核退回:3:515068", null, null);
//        StartProcessInstanceParam.ByKey key = StartProcessInstanceParam.createKey("审核退回",null,null,null);
//        StartProcessInstanceParam.ByMessage message = StartProcessInstanceParam.createMessage("start",null,null,null);

        ProcessInstanceEntityCustom instance = processInstances.startProcessInstance(new Gson().toJson(id));
//        ProcessInstanceEntityCustom instance1 = processInstances.startProcessInstance(new Gson().toJson(key));
//        ProcessInstanceEntityCustom instance2 = processInstances.startProcessInstance(new Gson().toJson(message));
        System.out.println(instance);
//        System.out.println(instance1);
//        System.out.println(instance2);
    }
}