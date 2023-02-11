package com.ydl.sms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ydl.sms.entity.ReceiveLogEntity;
import com.ydl.sms.vo.ReceiveLogVO;
import com.ydl.sms.vo.StatisticsCountVO;

import java.util.List;
import java.util.Map;

/**
 * 接收日志表
 *
 * @author IT李老师
 *
 */
public interface ReceiveLogService extends IService<ReceiveLogEntity> {

    Page<ReceiveLogVO> pageLog(Page<ReceiveLogVO> page, Map<String, Object> params);

    List<StatisticsCountVO> top10(Map params);

    List<StatisticsCountVO> trend(Map params);
}
