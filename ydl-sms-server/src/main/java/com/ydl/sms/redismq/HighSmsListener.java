package com.ydl.sms.redismq;

import com.ydl.sms.factory.SmsFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * Redis队列-----消费者
 * 监听消息队列：TOPIC_HIGH_SMS，高优先级的短信，如验证码之类的短信
 */
@Component
@Slf4j
public class HighSmsListener extends Thread {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SmsFactory smsFactory;

    private String queueKey = "TOPIC_HIGH_SMS";

    @Value("${spring.redis.queue.pop.timeout}")
    private Long popTimeout = 8000L;

    private ListOperations listOps;

    @PostConstruct
    private void init() {
        listOps = redisTemplate.opsForList();
        this.start();
    }

    @Override
    public void run() {
        //TODO 监听TOPIC_HIGH_SMS队列，如果有消息则调用短信发送工厂发送实时短信
        //监听    TOPIC_HIGH_SMS 发送
        while (true){
            log.debug("队列{}正在监听中",queueKey);
            //SmsSendDTO -->string
            String message = (String) listOps.rightPop(queueKey, popTimeout, TimeUnit.MILLISECONDS);
            if(StringUtils.isNotBlank(message)){
                log.info("{}收到消息了：{}",queueKey,message);
                //发送
                smsFactory.send(message);
            }
        }
    }
}
