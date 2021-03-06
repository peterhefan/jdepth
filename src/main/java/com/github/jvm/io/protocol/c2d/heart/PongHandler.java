package com.github.jvm.io.protocol.c2d.heart;

import com.github.jvm.io.protocol.c2d.message.C2DHeader;
import com.github.jvm.io.protocol.c2d.message.C2DMessage;
import com.github.jvm.io.protocol.c2d.message.MessageSignal;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PongHandler extends ChannelHandlerAdapter {

    private final static Logger logger = LoggerFactory.getLogger(PongHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        C2DMessage message = (C2DMessage) msg;
        // 返回心跳应答消息
        if (message.getHeader() != null && message.getHeader().getSignal() == MessageSignal.PING) {
            logger.info("Receive client heart beat message : ---> " + message);
            C2DMessage heartBeat = buildHeatBeat(message.getHeader().getSignal());
            logger.info("Send heart beat response message to client : ---> " + heartBeat);
            ctx.writeAndFlush(heartBeat);
        } else
            ctx.fireChannelRead(msg);
    }

    private C2DMessage buildHeatBeat(long msgId) {
        C2DMessage message = new C2DMessage();
        C2DHeader header = new C2DHeader();
        header.setSerial(msgId);
        header.setSignal(MessageSignal.PONG);
        message.setHeader(header);
        return message;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("PongHandler exception", cause);
        ctx.close();
        ctx.fireExceptionCaught(cause);
    }
}
