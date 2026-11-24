package com.ticketing.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Value("${spring.data.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.data.redis.port:6379}")
    private int redisPort;

    @Value("${spring.data.redis.password:}")
    private String redisPassword;

    @Value("${spring.data.redis.ssl.enabled:false}")
    private boolean redisSslEnabled;

    /**
     * Yerel (redis://localhost:6379) ve bulut (Upstash/Redis Cloud rediss:// + şifre)
     * ortamlarını otomatik algılayan RedissonClient yapılandırması.
     */
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() {
        Config config = new Config();
        String protocol = redisSslEnabled ? "rediss://" : "redis://";
        SingleServerConfig serverConfig = config.useSingleServer()
                .setAddress(protocol + redisHost + ":" + redisPort)
                .setConnectTimeout(5000)
                .setTimeout(5000)
                .setRetryAttempts(2)
                .setConnectionMinimumIdleSize(1)
                .setConnectionPoolSize(8);

        if (redisPassword != null && !redisPassword.trim().isEmpty()) {
            serverConfig.setPassword(redisPassword.trim());
        }

        return Redisson.create(config);
    }
}
