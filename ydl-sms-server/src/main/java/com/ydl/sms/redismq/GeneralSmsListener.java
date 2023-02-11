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
 * Redis队列消费者，监听消息队列TOPIC_GENERAL_SMS，普通优先级的短信，如营销短信
 */
@Component
@Slf4j
public class GeneralSmsListener extends Thread {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SmsFactory smsFactory;

    private String queueKey = "TOPIC_GENERAL_SMS";

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
        //监听消息队列，进行发送时时的消息
        while (true){
            String message = (String)listOps.rightPop(queueKey, popTimeout, TimeUnit.SECONDS);
            if (StringUtils.isNotEmpty(message)){
                log.debug("队列{}正在监听中",queueKey);
                log.info("监听{}队列需要发送的消息有:{}",queueKey,message);

                //是否应该往redis当中存一份，进行短信勾兑
               smsFactory.send(message);
            }
        }
    }
}
