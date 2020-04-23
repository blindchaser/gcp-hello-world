package com.sabre.labs.gcpdemo;

import com.sabre.labs.gcpdemo.spanner.SpannerSchemaTool;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@EnableFeignClients
@SpringBootApplication
@EnableRedisRepositories
@RequiredArgsConstructor
public class GcpDemoApplication {

    private final SpannerSchemaTool spannerSchemaTool;

    public static void main(String[] args) {
        SpringApplication.run(GcpDemoApplication.class, args);
    }

    @Bean
    public RedisTemplate<?, ?> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<?, ?> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    @Bean
    ApplicationRunner applicationRunner() {
        return (args) -> this.spannerSchemaTool.setUp();
    }
}
