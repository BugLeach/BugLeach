package com.ydl.sms.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

//普通类--》reids监听类
@Component
@Slf4j
public class MyListener implements MessageListener {

    //接受到redis消息时，干的事儿
    @Override
    public void onMessage(Message message, byte[] bytes) {
        log.info("接收到了消息：{}",message);
    }
}
