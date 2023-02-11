package com.ydl.sms.job;

/**
 * 定时短信发送业务接口
 * 扫数据库的时候防止短信重复发送去获取锁
 */
public interface SendTimingSms {
    void execute(String timing) throws InterruptedException;
}
