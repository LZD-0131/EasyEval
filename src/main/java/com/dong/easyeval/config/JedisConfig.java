package com.dong.easyeval.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;


@Configuration
public class JedisConfig {
    @Value("${spring.redis.host}")
    private String redisHost;
    @Value("${spring.redis.port}")
    private int redisPort;
    @Value("${spring.redis.password}")
    private String password;
    @Value("${spring.redis.username}")
    private String username;

    @Bean
    public JedisPool jedisPool() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        // 配置连接池属性
        poolConfig.setMaxTotal(20);
        poolConfig.setMaxIdle(10);
        poolConfig.setJmxEnabled(false);
        
        // 如果用户名为空，使用不带用户名的构造函数进行认证
        if (username == null || username.trim().isEmpty()) {
            return new JedisPool(poolConfig, redisHost, redisPort, 2000, password);
        } else {
            return new JedisPool(poolConfig, redisHost, redisPort, username, password);
        }
    }
}
