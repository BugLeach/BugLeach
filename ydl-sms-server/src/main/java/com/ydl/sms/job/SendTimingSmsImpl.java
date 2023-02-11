package com.ydl.sms.job;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ydl.sms.entity.TimingPushEntity;
import com.ydl.sms.factory.SmsFactory;
import com.ydl.sms.mapper.TimingPushMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时短信业务处理器
 */
@Component
@Slf4j
public class SendTimingSmsImpl implements SendTimingSms {

    @Autowired
    private TimingPushMapper timingPushMapper;

    @Autowired
    private SmsFactory smsFactory;

    /**
     * 发送定时短信
     *
     * @param timing
     */
    @Override
    @Async
    public void execute(String timing) {//timing格式：yyyy-MM-dd HH:mm  2021-12-25 18:00
        //TODO 查询数据库获取本次需要发送的定时短信，调用短信工厂发送短信
        //1、查询需要发送的讯息
        LambdaQueryWrapper<TimingPushEntity> wrapper = new LambdaQueryWrapper<>();
       wrapper.eq(TimingPushEntity::getStatus,0);
       wrapper.eq(TimingPushEntity::getTiming,timing);
       wrapper.orderByAsc(TimingPushEntity::getCreateTime);
        List<TimingPushEntity> timingPushEntities = timingPushMapper.selectList(wrapper);
        log.info("这次要发送的短信{}条{}",timingPushEntities.size(),timingPushEntities);
        //2、通过工厂进行发送
        boolean send = smsFactory.send(JSON.toJSONString(timingPushEntities));
        //3、如果发送成功修改发送状态
        if (send){
            timingPushEntities.forEach(timingPushEntity -> {
                timingPushEntity.setStatus(1);
                timingPushEntity.setUpdateTime(LocalDateTime.now());
                timingPushEntity.setUpdateUser("System");
                timingPushMapper.updateById(timingPushEntity);
            });
            log.info("任务执行完毕"+timing);
        }
    }
}

































