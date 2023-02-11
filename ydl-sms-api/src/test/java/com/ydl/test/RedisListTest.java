package com.ydl.test;

import com.ydl.sms.SmsApiApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = SmsApiApplication.class)
public class RedisListTest {
    @Autowired
    RedisTemplate redisTemplate;

    //消息发送者
    //@Test
    public void testPush(){
        for (int i = 0; i < 10; i++) {
            redisTemplate.opsForList().leftPush("ydllist123", "msg"+i);
        }
    }

    //消息消费者
    //@Test
    public void testPop(){
        for (int i = 0; i < 11; i++) {
            Object value = redisTemplate.opsForList().rightPop("ydllist123");
            System.out.println("取出的值是："+value);
        }
    }

    //消费者阻塞监听队列
    //@Test
    public void testPopBlock(){
       while (true){
           Object value = redisTemplate.opsForList().rightPop("ydllist123", 10L, TimeUnit.SECONDS);
           //业务逻辑
           System.out.println("取出的值是："+value);
       }
    }



}
