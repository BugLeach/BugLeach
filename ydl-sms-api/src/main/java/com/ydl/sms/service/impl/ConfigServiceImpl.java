package com.ydl.sms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ydl.sms.entity.ConfigEntity;
import com.ydl.sms.mapper.ConfigMapper;
import com.ydl.sms.service.ConfigService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通道配置
 *
 * @author IT李老师
 *
 */
@Service
public class ConfigServiceImpl extends ServiceImpl<ConfigMapper, ConfigEntity> implements ConfigService {

    @Override
    public List<ConfigEntity> findByTemplateSignature(String template, String signature) {

        Map params = new HashMap();
        params.put("template", template);
        params.put("signature", signature);

        return baseMapper.findByTemplateSignature(params);
    }
}
