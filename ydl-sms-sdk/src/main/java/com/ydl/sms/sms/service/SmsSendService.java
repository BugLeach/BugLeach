package com.ydl.sms.sms.service;

import com.ydl.sms.sms.dto.R;
import com.ydl.sms.sms.dto.SmsBatchParamsDTO;
import com.ydl.sms.sms.dto.SmsParamsDTO;

public interface SmsSendService {
    R sendSms(SmsParamsDTO smsParamsDTO);

    R batchSendSms(SmsBatchParamsDTO smsBatchParamsDTO);
}
