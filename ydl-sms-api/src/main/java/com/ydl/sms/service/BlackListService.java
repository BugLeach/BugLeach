package com.ydl.sms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ydl.sms.entity.BlackListEntity;

import java.util.List;

/**
 * 黑名单
 *
 * @author IT李老师
 *
 */
public interface BlackListService extends IService<BlackListEntity> {

    List<String> listByType(String s);
}
