package com.ydl.sms.factory;

import com.alibaba.fastjson.JSON;
import com.ydl.sms.config.RedisLock;
import com.ydl.sms.entity.ConfigEntity;
import com.ydl.sms.entity.SmsConfig;
import com.ydl.sms.model.ServerTopic;
import com.ydl.sms.service.ConfigService;
import com.ydl.sms.service.SignatureService;
import com.ydl.sms.service.TemplateService;
import com.ydl.utils.SpringUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.awt.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 通道实例加载器
 * 执行时间：
 * 1、项目启动时
 * 2、通道重新排序时
 *
 *
 *
 * 就是构建新的Aliyun的通道（需要袋子靠反射，需要东西靠容器）
 */
@Component
@Slf4j
@Order(value = 101)
public class SmsConnectLoader implements CommandLineRunner {

    private static final List<Object> CONNECT_LIST = new ArrayList<>();

    private static String BUILD_NEW_CONNECT_TOKEN = null;

    private static List<ConfigEntity> FUTURE_CONFIG_LIST;

    @Autowired
    private ConfigService configService;

    @Autowired
    private RedisLock redisLock;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void run(String... args) {
        initConnect();
    }

    /**
     * 根据通道配置，初始化每个通道的bean对象
     */
    @SneakyThrows
    public void initConnect() {
        ArrayList<Object> conStructList = new ArrayList<>();
        //1.先要通过service找到可用通道，在这之前我们先搞一个list用来装通道
        List<ConfigEntity> configEntities = configService.listForConnect();
        configEntities.forEach(configEntity -> {
            try {
                SmsConfig smsConfig = new SmsConfig();
                smsConfig.setId(configEntity.getId());
                smsConfig.setDomain(configEntity.getDomain());
                smsConfig.setName(configEntity.getName());
                smsConfig.setPlatform(configEntity.getPlatform().trim());
                smsConfig.setAccessKeyId(configEntity.getAccessKeyId().trim());
                smsConfig.setAccessKeySecret(configEntity.getAccessKeySecret().trim());
                if (StringUtils.isNotBlank(configEntity.getOther())) {
                    LinkedHashMap otherConfig = JSON.parseObject(configEntity.getOther(), LinkedHashMap.class);
                    smsConfig.setOtherConfig(otherConfig);
                }
                //2.用反射拿取已经注册的通道;
                String className = "com.ydl.sms.sms." + configEntity.getPlatform() + "SmsService";
                Class<?> aClass = Class.forName(className);
                //直接拿实例的话，无法将smsConfig传进去，我们只能利用构造器传进去
                Constructor<?> constructor = aClass.getConstructor(SmsConfig.class);

                Object obj = constructor.newInstance(smsConfig);

                //现在的问题是，我们没有办法设置通道的签名和属性,所以我们通过反射获取通道的父类，这里是添的
                Class<?> superclass = aClass.getSuperclass();
                Field signatureServiceFiled = superclass.getDeclaredField("signatureService");
                Field templateServiceFiled = superclass.getDeclaredField("templateService");
                //从容器中获取service
                //这里是被添的
                SignatureService signatureService = SpringUtils.getBean(SignatureService.class);
                TemplateService templateService = SpringUtils.getBean(TemplateService.class);
                signatureServiceFiled.setAccessible(true);
                templateServiceFiled.setAccessible(true);
                signatureServiceFiled.set(obj, signatureService);
                templateServiceFiled.set(obj, templateService);
                //3.把每一个的通道实例，装在集合里面
                conStructList.add(obj);
                log.info("{}通道加载完成",configEntity.getName());
            } catch (Exception e) {
                log.warn("{}通道加载失败",configEntity.getName());
                log.warn("{}失败信息",e.getMessage());
            }
        });
        if (!CONNECT_LIST.isEmpty()) {
            CONNECT_LIST.clear();
        }
        CONNECT_LIST.addAll(conStructList);
        //解锁逻辑(新通道要改变通道)多系统下
        if (StringUtils.isNotBlank(BUILD_NEW_CONNECT_TOKEN)){
            redisLock.unlock("buildNewConnect",BUILD_NEW_CONNECT_TOKEN);
        }
        log.info("{}通道初始化完成了。", CONNECT_LIST);
    }

    public <T> T getConnectByLevel(Integer level) {
        return (T) CONNECT_LIST.get(level - 1);
    }

    public boolean checkConnectLevel(Integer level) {
        return CONNECT_LIST.size() <= level;
    }

    /**
     * 通道调整：
     * 通道初始化：构建新的通道配置
     *
     *
     *
     *
     *通道选举【轻微】
     * 只能有一台机器执行，所以需要加锁
     */
    public void buildNewConnect() {
        // 一小时内有效
        String token = redisLock.tryLock("buildNewConnect", 1000 * 60 * 60 * 1);
        log.info("buildNewConnect token:{}", token);
        if (StringUtils.isNotBlank(token)) {
            List<ConfigEntity> list = configService.listForNewConnect();
            FUTURE_CONFIG_LIST = list;
            redisTemplate.opsForValue().set("NEW_CONNECT_SERVER", ServerRegister.SERVER_ID);
            BUILD_NEW_CONNECT_TOKEN = token;
        }
        // 获取不到锁 证明已经有服务在计算或者计算结果未得到使用
    }

    /**
     * 通道调整：
     * 发布订阅消息，通知其他服务：应用新的通道
     * 【严重】
     */
    public void changeNewConnectMessage() {
        redisTemplate.convertAndSend("TOPIC_HIGH_SERVER", ServerTopic.builder().option(ServerTopic.USE_NEW_CONNECT).value(ServerRegister.SERVER_ID).build().toString());
    }
    /**
     * 【严重】
     * 通道调整
     * 发布订阅消息，通知其他服务：初始化新通道
     */
    public void changeNewConnect() {
        // 初始化通道
        Object newConnectServer = redisTemplate.opsForValue().get("NEW_CONNECT_SERVER");

        /**
         * 为了通道调整发布的消息中，带有server id
         * 确保只有此server id的服务执行当前代码
         */
        if (null != newConnectServer && ServerRegister.SERVER_ID.equals(newConnectServer) &&
                !CollectionUtils.isEmpty(FUTURE_CONFIG_LIST)) {
            // 配置列表不为空则执行数据库操作 并清空缓存
            boolean result = configService.updateBatchById(FUTURE_CONFIG_LIST);
            log.info("批量修改配置级别:{}", result);
            FUTURE_CONFIG_LIST.clear();
            redisTemplate.convertAndSend("TOPIC_HIGH_SERVER", ServerTopic.builder().option(ServerTopic.INIT_CONNECT).value(ServerRegister.SERVER_ID).build().toString());
        }
    }
}
