package com.github.jvm.io.netty.fixedlen;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

public class EchoClient {

    private static final String HOST = "127.0.0.1";
    private static final int    PORT =        8020;

    public static void main(String... args) {

        try {
            new EchoClient().connect(HOST, PORT);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void connect(String host, int port) throws InterruptedException {

        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                            ch.pipeline()
                                    .addLast(new FixedLengthFrameDecoder(15)) // 定长设置
                                    .addLast(new StringDecoder())
                                    .addLast(new EchoClientHandler());
                        }
                    });

            ChannelFuture future = bootstrap.connect(host, port).sync();
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    class EchoClientHandler extends ChannelHandlerAdapter {

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {

            for (int i = 10; i < 100; i++) {
                ctx.writeAndFlush(Unpooled.copiedBuffer(("Echo request " + i).getBytes()));
            }
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

            System.out.println("Response is >> " + msg);
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            ctx.flush();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

            cause.printStackTrace();
            ctx.close();
        }
    }
}
