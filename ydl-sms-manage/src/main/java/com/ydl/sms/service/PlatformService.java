package com.ydl.sms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ydl.sms.entity.PlatformEntity;

/**
 * 接入平台
 *
 * @author IT李老师
 *
 */
public interface PlatformService extends IService<PlatformEntity> {

    PlatformEntity getByName(String name);
}
