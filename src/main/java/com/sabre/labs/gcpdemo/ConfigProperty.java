package com.sabre.labs.gcpdemo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "spring.cloud.gcp")
public class ConfigProperty {
    public String bucketName;

}
