package com.ydl.sms.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ydl.sms.entity.PlatformEntity;
import com.ydl.sms.mapper.PlatformMapper;
import com.ydl.sms.service.PlatformService;
import org.springframework.stereotype.Service;

/**
 * 接入平台
 *
 * @author IT李老师
 *
 */
@Service
public class PlatformServiceImpl extends ServiceImpl<PlatformMapper, PlatformEntity> implements PlatformService {

    @Override
    public PlatformEntity getByName(String name) {
        LambdaUpdateWrapper<PlatformEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(PlatformEntity::getName, name);
        return this.getOne(wrapper);
    }
}
