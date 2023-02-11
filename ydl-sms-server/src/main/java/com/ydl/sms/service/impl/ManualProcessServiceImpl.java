package com.ydl.sms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ydl.sms.entity.ManualProcessEntity;
import com.ydl.sms.mapper.ManualProcessMapper;
import com.ydl.sms.service.ManualProcessService;
import org.springframework.stereotype.Service;

/**
 * 人工处理任务表
 *
 * @author IT李老师
 *
 */
@Service
public class ManualProcessServiceImpl extends ServiceImpl<ManualProcessMapper, ManualProcessEntity> implements ManualProcessService {

}
