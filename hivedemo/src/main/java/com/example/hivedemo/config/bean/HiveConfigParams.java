package com.example.hivedemo.config.bean;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "hive")
public class HiveConfigParams {
    private String url;
    private String driverClassName;
    private String userName;
    private String password;
}
