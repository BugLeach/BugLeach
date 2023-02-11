package com.ydl.sms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ydl.sms.entity.SendLogEntity;
import com.ydl.sms.vo.MarketingStatisticsCountVO;
import com.ydl.sms.vo.SendLogPageVO;
import com.ydl.sms.vo.SendLogVO;
import com.ydl.sms.vo.StatisticsCountVO;

import java.util.List;
import java.util.Map;

/**
 * 日志表
 *
 * @author IT李老师
 *
 */
public interface SendLogService extends IService<SendLogEntity> {

    Page<SendLogVO> pageLog(Page<SendLogVO> page, Map<String, Object> params);

    List<StatisticsCountVO> trend(Map params);

    Page<StatisticsCountVO> countPage(Page<StatisticsCountVO> page, Map<String, Object> params);

    List<Map> countForConfig(Map params);

    List<Map> rateForConfig(Map params);

    MarketingStatisticsCountVO getMarketingCountByBusinessId(String id);

    Page<SendLogPageVO> sendLogPage(Page<SendLogPageVO> page, SendLogPageVO sendLogPageVO);
}
