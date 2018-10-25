package cn.adbyte.activiti;

import feign.Feign;
import feign.Logger;
import feign.auth.BasicAuthRequestInterceptor;
import feign.okhttp.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Adam
 */
@Configuration
public class FeignConfig {

    private final BasicAuthRequestInterceptor AUTH = new BasicAuthRequestInterceptor("fozzie", "fozzie");
    private final Logger.JavaLogger LOGGER = new Logger.JavaLogger().appendToFile("http.log");

    @Bean
    public Feign.Builder builder() {
        return Feign.builder().client(new OkHttpClient()).logger(LOGGER).logLevel(Logger.Level.FULL).requestInterceptor(AUTH);
    }

}
