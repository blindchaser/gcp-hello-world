package com.sabre.labs.gcpdemo;

import com.sabre.labs.gcpdemo.spanner.SpannerSchemaTool;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@EnableFeignClients
@SpringBootApplication
@RequiredArgsConstructor
public class GcpDemoApplication {
    private final SpannerSchemaTool spannerSchemaTool;

    public static void main(String[] args) {
        SpringApplication.run(GcpDemoApplication.class, args);
    }

    @Bean
    ApplicationRunner applicationRunner() {
        return (args) -> this.spannerSchemaTool.setUp();
    }
}
