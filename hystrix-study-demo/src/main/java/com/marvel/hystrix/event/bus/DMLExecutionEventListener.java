/*
 * Copyright (C) 2009-2018 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package com.marvel.hystrix.event.bus;

import com.alibaba.fastjson.JSON;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;

/**
 * DMLExecutionEventListener
 * 事件监听器
 *
 * @author luobosi@2dfire.com
 * @since 2018-12-28
 */
public class DMLExecutionEventListener {

    @Subscribe
    @AllowConcurrentEvents
    public void add(final DMLExecutionEvent event) {
        System.out.println("监听的添加执行事件: " + JSON.toJSONString(event));
        // do something
    }

    public void update(final DMLExecutionEvent event) {

    }
}
