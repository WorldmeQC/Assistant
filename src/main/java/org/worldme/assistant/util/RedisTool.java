package org.worldme.assistant.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Properties;

/**
 * @Author WorldmeQC
 * @Time 2022/10/13 13:36
 **/
public class RedisTool {
    private static String host;
    private static int port;
    private static String password;
    private static int timeout;
    private static JedisPool pool;

    static {
        YamlPropertiesFactoryBean yamlProperties = new YamlPropertiesFactoryBean();
        yamlProperties.setResources(new ClassPathResource("application.yml"));
        Properties properties = yamlProperties.getObject();
        host = properties.getProperty("spring.data.redis.host");
        port = Integer.parseInt(properties.getProperty("spring.data.redis.port"));
        password = properties.getProperty("spring.data.redis.password");
        timeout = Integer.parseInt(properties.getProperty("spring.data.redis.timeout"));
        JedisPoolConfig config = new JedisPoolConfig();
        pool = new JedisPool(new JedisPoolConfig(), host, port, timeout, password);
    }

    public static Jedis getResource() {
        Jedis jedis = pool.getResource();
        return jedis;
    }
}
