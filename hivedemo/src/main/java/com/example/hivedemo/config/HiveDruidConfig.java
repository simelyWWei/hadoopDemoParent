package com.example.hivedemo.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.example.hivedemo.config.bean.DataSourceConfigParams;
import com.example.hivedemo.config.bean.HiveConfigParams;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;
import javax.sql.DataSource;

@Slf4j
@Configuration
public class HiveDruidConfig {

   @Resource
    private DataSourceConfigParams dataSourceConfigParams;

    @Resource
    private HiveConfigParams hiveConfigParams;

    @Bean(name = "hiveDruidDataSource")
    @Qualifier("hiveDruidDataSource")
    public DataSource dataSource(){

        DruidDataSource datasource = new DruidDataSource();
        // hive configuration
        datasource.setUrl(hiveConfigParams.getUrl());
        datasource.setUsername(StringUtils.defaultIfBlank(hiveConfigParams.getUserName(),""));
        datasource.setPassword(StringUtils.defaultIfBlank(hiveConfigParams.getPassword(),""));
        datasource.setDriverClassName(hiveConfigParams.getDriverClassName());

        // pool configuration
        datasource.setInitialSize(dataSourceConfigParams.getInitialSize());
        datasource.setMinIdle(dataSourceConfigParams.getMinIdle());
        datasource.setMaxActive(dataSourceConfigParams.getMaxActive());
        datasource.setMaxWait(dataSourceConfigParams.getMaxWait());
        datasource.setTimeBetweenEvictionRunsMillis(dataSourceConfigParams.getTimeBetweenEvictionRunsMillis());
        datasource.setMinEvictableIdleTimeMillis(dataSourceConfigParams.getMinEvictableIdleTimeMillis());
        datasource.setValidationQuery(dataSourceConfigParams.getValidationQuery());
        datasource.setTestWhileIdle(dataSourceConfigParams.isTestWhileIdle());
        datasource.setTestOnBorrow(dataSourceConfigParams.isTestOnBorrow());
        datasource.setTestOnReturn(dataSourceConfigParams.isTestOnReturn());
        datasource.setPoolPreparedStatements(dataSourceConfigParams.isPoolPreparedStatements());
        datasource.setMaxPoolPreparedStatementPerConnectionSize(dataSourceConfigParams.getMaxPoolPreparedStatementPerConnectionSize());
        log.info("Hive DataSource Inject Successfully.");
        return datasource;
    }

    @Bean(name = "hiveDruidTemplate")
    public JdbcTemplate hiveDruidTemplate(@Qualifier("hiveDruidDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
