package com.ydl.sms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ydl.sms.dto.ConfigDTO;
import com.ydl.sms.entity.ConfigSignatureEntity;

/**
 * 配置—签名表
 */
public interface ConfigSignatureService extends IService<ConfigSignatureEntity> {

    void merge(ConfigDTO entity);
}
