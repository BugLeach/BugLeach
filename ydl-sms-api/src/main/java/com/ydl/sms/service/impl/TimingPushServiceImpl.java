package com.ydl.sms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ydl.sms.entity.TimingPushEntity;
import com.ydl.sms.mapper.TimingPushMapper;
import com.ydl.sms.service.TimingPushService;
import org.springframework.stereotype.Service;

/**
 * 定时发送
 *
 * @author IT李老师
 *
 */
@Service
public class TimingPushServiceImpl extends ServiceImpl<TimingPushMapper, TimingPushEntity> implements TimingPushService {

}
