/*
 * Copyright (C) 2009-2018 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package com.marvel.hystrix.event.bus;

import com.google.common.eventbus.EventBus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * EventBusInstance
 *
 * @author luobosi@2dfire.com
 * @since 2018-12-28
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventBusInstance {

    private static final EventBus INSTANCE = new EventBus();

    public static EventBus getInstance() {
        return INSTANCE;
    }
}
