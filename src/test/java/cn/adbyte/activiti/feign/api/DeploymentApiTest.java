package cn.adbyte.activiti.feign.api;

import cn.adbyte.activiti.FeignConfig;
import cn.adbyte.activiti.feign.api.entity.DeployResourceEntityCustom;
import cn.adbyte.activiti.feign.decoder.BytesDecoder;
import cn.adbyte.activiti.feign.decoder.StatusDecoder;
import feign.Feign;
import feign.form.FormEncoder;
import feign.gson.GsonDecoder;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@Import({FeignConfig.class})
public class DeploymentApiTest {

    private final String url = "http://localhost:8080/activiti-rest/service";

    @Autowired
    private Feign.Builder builder;

    private DeploymentApi deployment;
    private DeploymentApi uploadDeployment;
    private DeploymentApi downloadDeployment;
    private DeploymentApi statusDeployment;

    @PostConstruct
    private void init(){
        GsonDecoder gsonDecoder = new GsonDecoder();
        deployment = builder.decoder(gsonDecoder).target(DeploymentApi.class, url);
        uploadDeployment = builder.encoder(new FormEncoder()).decoder(gsonDecoder).target(DeploymentApi.class, url);
        downloadDeployment = builder.decoder(new BytesDecoder()).target(DeploymentApi.class, url);
        statusDeployment = builder.decoder(new StatusDecoder()).target(DeploymentApi.class, url);
    }

    @Test
    public void listDeployments() {
        Map<String, Object> map = new HashMap<>();
//        map.put("nameLike", "_");
        System.out.println(deployment.listDeployments(map));
    }

    @Test
    public void getDeployment() {
        System.out.println(deployment.getDeployment("512517"));
    }

    @Test
    public void createDeployment() {
        File file = new File("src/main/resources/deploy/审核退回.bpmn20.xml");
        System.out.println(uploadDeployment.createDeployment(file));
    }

    @Test
    public void deleteDeployment() {
        System.out.println(statusDeployment.deleteDeployment("512577"));
    }

    @Test
    public void listResourcesByDeploymentId() {
        List<DeployResourceEntityCustom> depolyResouceEntityCustoms = deployment.listResources("512517");
        System.out.println(depolyResouceEntityCustoms);
    }

    @Test
    public void getResources() {
        DeployResourceEntityCustom depolyResouceEntityCustoms = deployment.getResources("512517", "DeploymentUpload.bpmn20.xml");
        System.out.println(depolyResouceEntityCustoms);
    }


    @Test
    public void getResourceData() throws IOException {

        String fileName = "startByMessage.bpmn20.xml";
        byte[] bytes = downloadDeployment.getResourceData("512577", fileName);

        String userHomeDir = "src/main/resources/generated/";
        InputStream inputStream = new ByteArrayInputStream(bytes);
        FileUtils.copyInputStreamToFile(inputStream, new File(userHomeDir + fileName));
        System.out.println("done");
    }
}