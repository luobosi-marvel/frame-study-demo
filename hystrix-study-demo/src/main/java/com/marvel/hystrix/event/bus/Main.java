/*
 * Copyright (C) 2009-2018 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package com.marvel.hystrix.event.bus;

import com.alibaba.fastjson.JSON;

import java.util.Date;
import java.util.Random;

/**
 * Main
 *
 * @author luobosi@2dfire.com
 * @since 2018-12-28
 */
public class Main {

    static {
        System.out.println("register listener...");
        EventBusInstance.getInstance().register(new DMLExecutionEventListener());
    }

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            pub();
            Thread.sleep(3000);

        }
    }

    private static void pub() {
        DMLExecutionEvent event = new DMLExecutionEvent();
        event.setId(String.valueOf(new Random().nextInt(1000)));
        event.setDataSource("marvel_db_1");
        event.setSendTime(new Date());
        System.out.println("发布的事件：" + JSON.toJSONString(event));
        EventBusInstance.getInstance().post(event);
    }
}
