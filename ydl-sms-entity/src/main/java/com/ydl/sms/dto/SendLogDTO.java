package com.ydl.sms.dto;

import com.ydl.sms.entity.SendLogEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;


/**
 * 日志表
 *
 * @author IT李老师
 *
 */
@Data
@ApiModel(description = "日志表")
public class SendLogDTO extends SendLogEntity {

}
