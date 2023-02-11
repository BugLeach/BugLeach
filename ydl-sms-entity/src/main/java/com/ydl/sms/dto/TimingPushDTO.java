package com.ydl.sms.dto;

import com.ydl.sms.entity.TimingPushEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * 定时发送
 *
 * @author IT李老师
 */
@Data
@ApiModel(description = "定时发送")
public class TimingPushDTO extends TimingPushEntity {

}
