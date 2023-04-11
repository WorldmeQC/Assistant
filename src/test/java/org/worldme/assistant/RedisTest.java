package org.worldme.assistant;

import org.junit.jupiter.api.Test;
import org.worldme.assistant.util.RedisTool;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @Author WorldmeQC
 * @Time 2023/4/6 17:30
 **/
public class RedisTest {

    @Test
    public void test(){
        JedisPool pool = new JedisPool("47.120.35.241",6379);
        Jedis jedis = pool.getResource();
        jedis.auth("Wanghx8870639!");
        jedis.set("1","2121");
    }
}
