package com.ydl.test;

import com.ydl.sms.entity.ConfigEntity;
import com.ydl.sms.service.ConfigService;

import java.util.List;

//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = SmsServerApplication.class)
public class Test {
    //@Autowired
    private ConfigService configService;

    //@org.junit.Test
    public void test1(){
        List<ConfigEntity> configEntities = configService.listForConnect();
        System.out.println(configEntities);
    }

    //@org.junit.Test
    public void test2(){
        List<ConfigEntity> configEntities = configService.listForNewConnect();
        System.out.println(configEntities);
    }
}
