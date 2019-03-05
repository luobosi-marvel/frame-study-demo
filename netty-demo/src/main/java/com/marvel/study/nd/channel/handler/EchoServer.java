/*
 * Copyright (C) 2009-2018 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package com.marvel.study.nd.channel.handler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * EchoServer
 *
 * @author luobosi@2dfire.com
 * @since 2019-02-27
 */
public class EchoServer {



    private final  int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws InterruptedException {
        int port = 8080;
        ByteBufAllocator allocator;

        new EchoServer(port).start();
    }

    public void start() throws InterruptedException {
        final EchoServerHandler echoServerHandler = new EchoServerHandler();

        final EchoOtherServerHandler echoOtherServerHandler = new EchoOtherServerHandler();

        final EchoServerOutHandler echoServerOutHandler = new EchoServerOutHandler();

        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(eventLoopGroup).
                    //指定channel使用Nio传输
                    channel(NioServerSocketChannel.class).
                    //执行端口设置套接字地址
                    localAddress(new InetSocketAddress(port)).
                    //添加 echoServerHandler 到 Channel 的 ChannelPipeline 上
                    childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    ChannelPipeline channelPipeline = socketChannel.pipeline();
                    channelPipeline.addFirst(echoOtherServerHandler);
                    channelPipeline.addFirst(echoServerOutHandler);
                    channelPipeline.addLast(echoServerHandler);
                }
            });
            //异步绑定服务器，调用sync()方法阻塞等待直到绑定完成
            ChannelFuture f = serverBootstrap.bind().sync();
            //获得Channel的 close future，并且阻塞当前线程直到它完成
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            eventLoopGroup.shutdownGracefully().sync();
        }
    }
}
