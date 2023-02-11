package com.ydl.sms.dto;

import com.ydl.sms.entity.SignatureEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * 签名表
 *
 * @author IT李老师
 *
 */
@Data
@ApiModel(description = "签名表")
public class SignatureDTO extends SignatureEntity {

    @ApiModelProperty("是否选中")
    private boolean selected;

    @ApiModelProperty(value = "三方通道签名")
    private String configSignatureCode;

    @ApiModelProperty(value = "通道id")
    private String configId;
}
