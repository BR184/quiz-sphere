package com.kl.quizsphere.config;

import com.zhipu.oapi.ClientV4;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author <a href="https://github.com/BR184">BR184</a>
 * @version 1.0
 * @since 2024-09-05 15:32:12
 */
@Configuration
@ConfigurationProperties(prefix = "zhipu-ai.api")
@Data
public class ZhiPuAIConfig {
    /**
     * API秘钥
     */
    private String secretKey;
    @Bean
    public ClientV4 getClientV4() {
        return new ClientV4.Builder(secretKey).build();
    }
}
