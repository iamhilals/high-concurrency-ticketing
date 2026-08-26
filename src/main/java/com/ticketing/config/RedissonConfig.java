package com.ticketing.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    /**
     * RedissonClient nesnesini Spring IoC Container'a kaydeder.
     * Bu sayede istediğimiz servis sınıfında @Autowired veya Constructor Injection ile enjekte edebiliriz.
     */
    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        
        // Yerel Redis sunucusuna (localhost:6379) tekil sunucu (single server) olarak bağlanacak şekilde yapılandırıyoruz.
        // Adres protokolü mutlaka "redis://" (veya SSL için "rediss://") ile başlamalıdır.
        config.useSingleServer()
              .setAddress("redis://127.0.0.1:6379");
              
        return Redisson.create(config);
    }
}
