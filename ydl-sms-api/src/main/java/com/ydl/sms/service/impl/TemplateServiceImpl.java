package com.ydl.sms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ydl.sms.entity.TemplateEntity;
import com.ydl.sms.mapper.TemplateMapper;
import com.ydl.sms.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 短信模板
 *
 * @author IT李老师
 *
 */
@Service
public class TemplateServiceImpl extends ServiceImpl<TemplateMapper, TemplateEntity> implements TemplateService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public TemplateEntity getByCode(String template) {
        ValueOperations<String, TemplateEntity> ops = redisTemplate.opsForValue();
        TemplateEntity templateEntity = ops.get(template);
        if (templateEntity == null) {
            LambdaQueryWrapper<TemplateEntity> wrapper = new LambdaQueryWrapper();
            wrapper.eq(TemplateEntity::getCode, template);
           templateEntity= baseMapper.selectOne(wrapper);
            ops.set(template, templateEntity, 60, TimeUnit.SECONDS);
        }
        return templateEntity;
    }
}
