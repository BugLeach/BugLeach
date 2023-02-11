package com.ydl.sms.sms.service.impl;

import com.alibaba.fastjson.JSON;
import com.ydl.sms.sms.dto.BaseParamsDTO;
import com.ydl.sms.sms.dto.R;
import com.ydl.sms.sms.dto.SmsBatchParamsDTO;
import com.ydl.sms.sms.dto.SmsParamsDTO;
import com.ydl.sms.sms.service.SmsSendService;
import com.ydl.sms.sms.utils.SmsEncryptionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * sdk为什么做了便利，只需接入发送的消息即可
 *
 * 其他的只需要在yml配置即可
 *
 * 发送到接收端还是要校验的
 */
@Service
@Slf4j
public class SmsSendServiceImpl implements SmsSendService {
    @Value("${ydlclass.sms.auth}")
    private boolean auth;
    @Value("${ydlclass.sms.domain}")
    private String domain;
    @Value("${ydlclass.sms.accessKeyId}")
    private String accessKeyId;
    @Value("${ydlclass.sms.accessKeySecret}")
    private String accessKeySecret;
    private String send = "/sms/send";
    private String batchSend = "/sms/batchSend";

    /**
     * 单条短信
     *
     * @param smsParamsDTO
     * @return
     */
    @Override
    public R sendSms(SmsParamsDTO smsParamsDTO) {
        String url = domain + send;
        return send(smsParamsDTO, url);
    }

    /**
     * 批量短信
     *
     * @param smsBatchParamsDTO
     * @return
     */
    @Override
    public R batchSendSms(SmsBatchParamsDTO smsBatchParamsDTO) {
        String url = domain + batchSend;
        return send(smsBatchParamsDTO, url);
    }

    /**
     * 通过HttpClient发送post请求，请求短信接收服务HTTP接口
     *
     * @param baseParamsDTO
     * @param url
     * @return
     */
    private R send(BaseParamsDTO baseParamsDTO, String url) {
        //电商 平台是否认证
        if (auth) {
            if (StringUtils.isBlank(accessKeyId) || StringUtils.isBlank(accessKeySecret)) {
                return R.fail("accessKeyId或者accessKeySecret不能为空");
            }
        }
        //设置此平台的秘钥
        baseParamsDTO.setAccessKeyId(accessKeyId);
        baseParamsDTO.setEncryption(SmsEncryptionUtils.encode(baseParamsDTO.getTimestamp(), accessKeyId, accessKeySecret));
        //发送之前判断一下domain不能为空
        if (StringUtils.isBlank(domain)) {
            return R.fail("domain不能为空");
        }
        //1 httpclien okhttp
        CloseableHttpClient httpClients = HttpClients.createDefault();
        //构造post请求
        HttpPost post = new HttpPost(url);
        //设置请求头
        post.setHeader("Content-Type", "application/json;charset=UTF-8");
        //设置请求体
        StringEntity stringEntity = new StringEntity(JSON.toJSONString(baseParamsDTO), "UTF-8");
        post.setEntity(stringEntity);
        //发送post请求
        try {
            CloseableHttpResponse response = httpClients.execute(post);
            //解析响应码
            if (response.getStatusLine().getStatusCode() == 200) {
                //发送成功
                log.info("发送成功");
                HttpEntity responseEntity = response.getEntity();
                String responseEntityStr = EntityUtils.toString(responseEntity);
                return JSON.parseObject(responseEntityStr, R.class);
            } else {
                //发送失败
                log.error("发送失败");
                return R.fail("发送失败", response.getStatusLine().getStatusCode());
            }
        } catch (Exception e) {
            log.error("发送异常");
            return R.fail("发送异常", e.getMessage());
        } finally {
            post.releaseConnection();
        }
    }
}
