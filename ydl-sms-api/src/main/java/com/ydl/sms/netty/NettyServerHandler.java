package com.ydl.sms.netty;

import com.alibaba.fastjson.JSON;
import com.ydl.sms.dto.SmsParamsDTO;
import com.ydl.sms.service.SmsSendService;
import com.ydl.utils.SpringUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.net.InetSocketAddress;

/**
 * 服务端处理器
 */
@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<String> {

    //业务逻辑
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        // TODO 短信接收服务：接收应用系统的报文并解析，调用Service将消息保存到消息缓冲区
        //就是和controller一样 调用service层即可
        String result = "success";
        try {
            log.info("tcp收到的所有的消息为" + msg);
            SmsParamsDTO smsParamsDTO = parseMessage(msg);
            if (smsParamsDTO == null) {
                log.info("报文字符串转化为对象的内容为空,出现异常");
                return;
            }
            SpringUtils.getBean(SmsSendService.class).send(smsParamsDTO);
        } catch (Exception e) {
            log.error("netty发送时报错了",e);
            result = e.getMessage();
        }
        log.info("回推报文=======================" + result);
        ctx.writeAndFlush(result + "\n");

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = insocket.getAddress().getHostAddress();
        log.info("收到客户端[ip:" + clientIp + "]连接");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 当出现异常就关闭连接
        ctx.close();
    }

    //永远不要相信别人

    /**
     * 解析报文
     * <p>
     * 设备不同报文也不同，直接使用json格式传输
     */
    private SmsParamsDTO parseMessage(String body) {
        if (org.apache.commons.lang.StringUtils.isBlank(body)) {
            log.warn("报文为空");
            return null;
        }
        body = body.trim();
        // 其它格式的报文需要解析后放入SmsParamsDTO实体
        SmsParamsDTO message = JSON.parseObject(body, SmsParamsDTO.class);
        if (message == null || org.apache.commons.lang.StringUtils.isBlank(message.getMobile()) || org.apache.commons.lang.StringUtils.isBlank(message.getSignature()) || StringUtils.isBlank(message.getTemplate())) {
            log.warn("报文内容异常");
            return null;
        }

        return message;
    }
}
