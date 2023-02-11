package com.ydl.sms.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ydl.sms.dto.ConfigDTO;
import com.ydl.sms.entity.ConfigEntity;
import com.ydl.sms.mapper.ConfigMapper;
import com.ydl.sms.model.ServerTopic;
import com.ydl.sms.service.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 通道配置表
 */
@Service
@Slf4j
public class ConfigServiceImpl extends ServiceImpl<ConfigMapper, ConfigEntity> implements ConfigService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public ConfigEntity getByName(String name) {
        LambdaUpdateWrapper<ConfigEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ConfigEntity::getName, name);
        return this.getOne(wrapper);
    }

    @Override
    public void getNewLevel(ConfigDTO entity) {
        LambdaUpdateWrapper<ConfigEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ConfigEntity::getIsEnable, 1);
        wrapper.eq(ConfigEntity::getIsActive, 1);
        wrapper.orderByDesc(ConfigEntity::getLevel);
        wrapper.last("limit 1");
        ConfigEntity configEntity = this.getOne(wrapper);
        if (configEntity == null) {
            entity.setLevel(1);
        } else {
            entity.setLevel(configEntity.getLevel() + 1);
        }
    }
//用来检查发送端的顺序是否正常（先找到正常的，再给人工排等级以后，redis当中的等级列表删除，让发送端重新放等级）
    @Override
    public void sendUpdateMessage() {
        // TODO 发送消息，通知短信发送服务更新内存中的通道优先级 redis存放发送端的存活的key : SERVER_ID_HASH
        //1 获取存活发送端
        Map map = redisTemplate.opsForHash().entries("SERVER_ID_HASH");
        log.info("全部的发送端有" + map);
        //获取当前时间
        long currentTimeMillis = System.currentTimeMillis();
        for (Object key : map.entrySet()) {
            Object value = map.get(key);
            long lastTime = Long.parseLong(value.toString());
            //如果小于5分钟才是上线的我们给他进行改变
            if ((currentTimeMillis - lastTime) < 1000 * 60 * 5) {
//已经上线了，但是重排了，所以发送的通道变了，所以我们应该删除通道的优先级
                redisTemplate.delete("listForConnect");
                //然后再去通知
                ServerTopic serverTopic = ServerTopic.builder().option(ServerTopic.INIT_CONNECT).value(key.toString()).build();
                redisTemplate.convertAndSend("TOPIC_HIGH_SERVER", serverTopic.toString());
                return;
            }
        }
    }
}
