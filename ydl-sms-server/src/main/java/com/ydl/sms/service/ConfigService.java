package com.ydl.sms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ydl.sms.entity.ConfigEntity;

import java.util.Collection;
import java.util.List;

/**
 * 配置表
 *
 * @author IT李老师
 *
 */
public interface ConfigService extends IService<ConfigEntity> {

    /**
     * 通道配置信息列表
     * @return
     */
    List<ConfigEntity> listForConnect();

    List<ConfigEntity> listForNewConnect();

    boolean updateBatchById(Collection<ConfigEntity> entityList);
}
