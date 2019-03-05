/*
 * Copyright (C) 2009-2018 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package com.marvel.study.nd.channel.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * EchoOtherServerHandler
 * TODO：注意，如果多个 client 连接的时候，EchoOtherServerHandler 一定要标识成 @ChannelHandler.Sharable
 * 否则会报错的
 *
 * @author luobosi@2dfire.com
 * @since 2019-02-27
 */
@ChannelHandler.Sharable
public class EchoOtherServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        System.out.println("Other注册事件");
        ctx.fireChannelRegistered();
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        System.out.println("Other取消注册事件");
        ctx.fireChannelUnregistered();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("Other有新客户端连接接入。。。" + ctx.channel().remoteAddress());
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("Other失去连接");
        ctx.fireChannelInactive();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf in = (ByteBuf) msg;
        System.out.println("Other读客户端传入数据=" + in.toString(CharsetUtil.UTF_8));
        final ByteBuf byteBuf = Unpooled.copiedBuffer("读入客户端传入数据后发送的数据：Other channelRead Netty rocks!", CharsetUtil.UTF_8);
        ctx.writeAndFlush(byteBuf);
        ctx.fireChannelRead(msg);
        //ReferenceCountUtil.release(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        // 添加一个监听器
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> future) {
                if (future.isSuccess()) {
                    System.out.println("Other执行成功=" + future.isSuccess());
                }
            }
        });
        final ByteBuf byteBuf = Unpooled.copiedBuffer("Other channelReadComplete Netty rocks!", CharsetUtil.UTF_8);
        ctx.writeAndFlush(byteBuf).addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> future) {
                if (future.isSuccess()) {
                    System.out.println("otherServerHandler writeAndFlush 成功");
                } else {
                    System.out.println("otherServerHandler writeAndFlush 失败");
                }
                // 这里会有个问题(如果这里画蛇添足再去释放 byteBuf 会报错的，因为 writeAndFlush 里面已经帮我们释放掉了)
                ReferenceCountUtil.release(byteBuf);
                // 这里我们会发现其实 byteBuf.refCnt() == 0
                // System.out.println("byteBuf.refCnt(): " + byteBuf.refCnt());
            }
        });
        ctx.fireChannelReadComplete();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        System.out.println("Other  userEventTriggered");
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) {
        System.out.println("Other  channelWritAbilityChanged");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
