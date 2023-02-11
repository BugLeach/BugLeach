package com.ydl.sms.sms;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;
import com.ydl.sms.entity.SignatureEntity;
import com.ydl.sms.entity.SmsConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 阿里云短信服务
 *
 * @author
 */
@Slf4j
public class AliyunSmsService extends AbstractSmsService {

    //private IClientProfile profile;
    Config aliconfig;

    public AliyunSmsService(SmsConfig config) {
        this.config = config;
        //初始化
        init();
    }

    private void init() {
        //初始化acsClient，暂不支持region化 "cn-hangzhou"
        //profile = DefaultProfile.getProfile(config.get("RegionId"), config.getAccessKeyId(), config.getAccessKeySecret());
        aliconfig = new Config()
                // 您的AccessKey ID
                .setAccessKeyId(config.getAccessKeyId())
                // 您的AccessKey Secret
                .setAccessKeySecret(config.getAccessKeySecret());
        // 访问的域名
        aliconfig.endpoint = "dysmsapi.aliyuncs.com";
    }

    @Override
    protected String sendSms(String mobile, Map<String, String> params, String signature, String template) {
        // 获取 签名内容 和模板id
        SignatureEntity signatureEntity = signatureService.getByCode(signature);
        String code = templateService.getConfigCodeByCode(config.getId(), template);
        try {
            Client client = new Client(aliconfig);

            SendSmsRequest request = new SendSmsRequest();
            request.setPhoneNumbers(mobile);
            request.setTemplateCode(code);
            request.setTemplateParam(JSON.toJSONString(params));
            request.setSignName(signatureEntity.getContent());

            SendSmsResponse response = client.sendSms(request);
            JSONObject jsonObject = JSON.parseObject(String.valueOf(response.getBody()));
            if (response.getBody().getCode().equals("OK")) {
                return response.getBody().toString();
            } else {
                return failResponse(jsonObject.getString("Message"), response.getBody().getMessage());
            }
        } catch (Exception e) {
            log.error("Aliyun 短信发送失败：{} ,{}", mobile, template, e);
            return failResponse(e.getMessage(), e.getMessage());
        }
        //"{\"Message\":\"OK\",\"RequestId\":\"" + UUID.randomUUID().toString().toUpperCase() + "-@\",\"BizId\":\"" + System.currentTimeMillis() + "\",\"Code\":\"OK\"}";
    }


    //https://help.aliyun.com/document_detail/55284.html?spm=5176.8195934.1284193.3.65f76a7di5WyeP
    //@Override
    //protected String sendSms(String mobile, Map<String, String> params, String signature, String template) {
    //    // 获取 签名内容 和模板id
    //    SignatureEntity signatureEntity = signatureService.getByCode(signature);
    //    String code = templateService.getConfigCodeByCode(config.getId(), template);
    //
    //    IAcsClient client = new DefaultAcsClient(profile);
    //
    //    CommonRequest request = new CommonRequest();
    //    request.setSysMethod(MethodType.POST);
    //    request.setSysDomain(config.getDomain());
    //    request.setSysVersion("2017-05-25");
    //    request.setSysAction("SendSms");
    //    request.putQueryParameter("RegionId", config.get("RegionId"));
    //    request.putBodyParameter("PhoneNumbers", mobile);
    //    request.putBodyParameter("SignName", signatureEntity.getContent());
    //    request.putBodyParameter("TemplateCode", code);
    //    request.putBodyParameter("TemplateParam", JSON.toJSONString(params));
    //
    //    try {
    //        CommonResponse response = client.getCommonResponse(request);
    //        JSONObject jsonObject = JSON.parseObject(response.getData());
    //        if (jsonObject.containsKey("Code") && jsonObject.getString("Code").equals("OK")) {
    //            return response.getData();
    //        } else {
    //            return failResponse(jsonObject.getString("Message"), response.getData());
    //        }
    //    } catch (Exception e) {
    //        log.error("Aliyun 短信发送失败：{} ,{}", mobile, template, e);
    //        return failResponse(e.getMessage(), e.getMessage());
    //    }
    //    //"{\"Message\":\"OK\",\"RequestId\":\"" + UUID.randomUUID().toString().toUpperCase() + "-@\",\"BizId\":\"" + System.currentTimeMillis() + "\",\"Code\":\"OK\"}";
    //}

    @Override
    protected String sendSmsBatch(String[] mobiles, LinkedHashMap<String, String>[] params, String[] signatures, String[] templates) {
        //IAcsClient client = new DefaultAcsClient(profile);
        //
        //CommonRequest request = new CommonRequest();
        //request.setSysMethod(MethodType.POST);
        //request.setSysDomain(config.getDomain());
        //request.setSysVersion(DateUtils.formatAsDate(LocalDateTime.now()));
        //request.setSysAction("SendBatchSms");
        //request.putQueryParameter("RegionId", config.get("RegionId"));
        //
        //if (mobiles.length <= 100) {
        //    request.putBodyParameter("PhoneNumbers", mobiles);
        //    request.putBodyParameter("SignName", signatures);
        //    request.putBodyParameter("TemplateCode", templates);
        //    request.putBodyParameter("TemplateParam", JSON.toJSONString(params));
        //    try {
        //        CommonResponse response = client.getCommonResponse(request);
        //        log.info(response.getData());
        //        return response.getData();
        //    } catch (ServerException e) {
        //        log.error("短信发送失败：{} ,{}", mobiles, templates, e);
        //    } catch (ClientException e) {
        //        log.error("短信发送失败：{} ,{}", mobiles, templates, e);
        //    }
        //} else {
        //    int batchCount = (mobiles.length / 100) + 1;
        //    for (int i = 0; i < batchCount; i++) {
        //        String[] newMobiles = Arrays.copyOfRange(mobiles, i * 100, (i + 1) * 100);
        //        String[] newSignNames = Arrays.copyOfRange(signatures, i * 100, (i + 1) * 100);
        //        String[] newTemplates = Arrays.copyOfRange(templates, i * 100, (i + 1) * 100);
        //        LinkedHashMap[] newParams = Arrays.copyOfRange(params, i * 100, (i + 1) * 100);
        //        request.putBodyParameter("PhoneNumbers", newMobiles);
        //        request.putBodyParameter("SignName", newSignNames);
        //        request.putBodyParameter("TemplateCode", newTemplates);
        //        request.putBodyParameter("TemplateParam", JSON.toJSONString(newParams));
        //        try {
        //            CommonResponse response = client.getCommonResponse(request);
        //            log.info(response.getData());
        //        } catch (ServerException e) {
        //            log.error("短信发送失败：{} ,{}", mobiles, templates, e);
        //        } catch (ClientException e) {
        //            log.error("短信发送失败：{} ,{}", mobiles, templates, e);
        //        }
        //    }
        //}
        return null;
    }
}
