package com.example.hivedemo.config.bean;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "datasource")
public class DataSourceConfigParams {

    private String type;
    /**
     * 初始化大小，最小，最大
     */
    private int initialSize;
    private int minIdle;
    private int maxActive;
    /**
     * 配置获取连接等待超时的时间
     */
    private int maxWait;
    /**
     * 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
     */
    private int timeBetweenEvictionRunsMillis;
    /**
     * 配置一个连接在池中最小生存的时间，单位是毫秒
     */
    private int minEvictableIdleTimeMillis;

    private String validationQuery;

    private boolean testWhileIdle;

    private boolean testOnBorrow;

    private boolean testOnReturn;
    /**
     * 打开PSCache，并且指定每个连接上PSCache的大小
     */
    private boolean poolPreparedStatements;
    private int maxPoolPreparedStatementPerConnectionSize;
}
