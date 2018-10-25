package cn.adbyte.activiti;

import cn.adbyte.activiti.feign.api.DeploymentApiTest;
import cn.adbyte.activiti.feign.test.FeignAuth;
import cn.adbyte.activiti.feign.test.FeignFirst;
import feign.Feign;
import feign.auth.BasicAuthRequestInterceptor;
import feign.gson.GsonDecoder;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;

/**
 * 更多测试参考
 * the {@link DeploymentApiTest} test class.
 */
public class _10REST服务Test {

    @Test
    public void CXF() throws IOException {
        // 创建WebClient
        WebClient client = WebClient.create("https://httpbin.org/basic-auth/user/passwd",
                "user", "passwd", null);
        // 获取响应
        Response response = client.get();
        // 获取响应内容
        InputStream ent = (InputStream) response.getEntity();
        String content = IOUtils.readStringFromStream(ent);
        // 输出字符串
        System.out.println(content);
    }

    @Test
    public void Feign() {
        // 获取服务接口
        FeignFirst client = Feign.builder()
                .decoder(new GsonDecoder())
                .target(FeignFirst.class, "https://httpbin.org/");
        // 请求接口
        System.out.println(client.getIp());
    }

    @Test
    public void feignBasicAuth() {
        // 获取服务接口
        FeignAuth client = Feign.builder()
                .decoder(new GsonDecoder())
                .requestInterceptor(new BasicAuthRequestInterceptor("user", "passwd"))
                .target(FeignAuth.class, "https://httpbin.org/");
        // 请求接口
        System.out.println(client.getAuthResult());
    }

}
