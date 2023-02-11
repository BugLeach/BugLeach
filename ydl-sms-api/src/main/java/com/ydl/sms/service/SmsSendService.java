package com.ydl.sms.service;

import com.ydl.sms.dto.SmsBatchParamsDTO;
import com.ydl.sms.dto.SmsParamsDTO;

public interface SmsSendService {
    void send(SmsParamsDTO smsParamsDTO);

    void batchSend(SmsBatchParamsDTO smsBatchParamsDTO);
}
