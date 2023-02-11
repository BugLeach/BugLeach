package com.ydl.test;

import com.ydl.sms.SmsManageApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = SmsManageApplication.class)
public class RedisTest {
    @Autowired
    private RedisTemplate redisTemplate;

    //@Test
    public void testSendToRedis(){
        redisTemplate.convertAndSend("MYTOPIC", "im itnanaoshi123");
    }

}
