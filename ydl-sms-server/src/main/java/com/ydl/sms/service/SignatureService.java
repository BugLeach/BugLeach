package com.ydl.sms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ydl.sms.entity.SignatureEntity;

/**
 * 签名表
 *
 * @author IT李老师
 *
 */
public interface SignatureService extends IService<SignatureEntity> {

    SignatureEntity getByCode(String signature);

    String getConfigCodeByCode(String id, String signature);
}
