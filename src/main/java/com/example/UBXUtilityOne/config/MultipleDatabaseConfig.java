package com.example.UBXUtilityOne.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class MultipleDatabaseConfig {

    @Bean(name = "db1")
    @ConfigurationProperties(prefix = "spring.customer-db")
    public DataSource dataSource1() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "jdbcTemplate1")
    public JdbcTemplate jdbcTemplate1(@Qualifier("db1") DataSource ds) {
        return new JdbcTemplate(ds);
    }

    @Bean(name = "db2")
    @ConfigurationProperties(prefix = "spring.entitlement-db")
    public DataSource dataSource2() {
        return  DataSourceBuilder.create().build();
    }

    @Bean(name = "jdbcTemplate2")
    public JdbcTemplate jdbcTemplate2po(@Qualifier("db2") DataSource ds) {
        return new JdbcTemplate(ds);
    }

    @Bean(name = "db3")
    @ConfigurationProperties(prefix = "spring.notification-db")
    public DataSource dataSource3() {
        return  DataSourceBuilder.create().build();
    }

    @Bean(name = "jdbcTemplate3")
    public JdbcTemplate jdbcTemplate3(@Qualifier("db3") DataSource ds) {
        return new JdbcTemplate(ds);
    }


}
