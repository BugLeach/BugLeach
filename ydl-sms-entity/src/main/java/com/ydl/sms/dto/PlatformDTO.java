package com.ydl.sms.dto;

import com.ydl.sms.entity.PlatformEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;


/**
 * 配置表
 *
 * @author IT李老师
 *
 */
@Data
@ApiModel(description = "接入平台")
public class PlatformDTO extends PlatformEntity {

}
