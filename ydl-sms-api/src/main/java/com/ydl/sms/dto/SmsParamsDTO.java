package com.ydl.sms.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@Data
@ApiModel( description = "短信发送参数")
public class SmsParamsDTO extends BaseParamsDTO {
    @ApiModelProperty("手机号")
    private String mobile;
    @ApiModelProperty("模板编码")
    private String template;
    @ApiModelProperty("签名编码")
    private String signature;
    @ApiModelProperty("参数")
    private Map<String, String> params;    //尊敬的${name}先生/女士，您尾号为${number}的卡，消费了${money}元。
                                           //name=>IT李老师 number=>1234 money=>10000

}
