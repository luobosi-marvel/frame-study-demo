/*
 * Copyright (C) 2009-2018 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package com.marvel.study.nd.reactor;

import lombok.Data;

/**
 * EventHandler
 * event 处理器的抽象类
 *
 * @author luobosi@2dfire.com
 * @since 2019-01-21
 */
@Data
public abstract class EventHandler {

    private InputSource source;

    /**
     * 具体的处理方法
     *
     * @param event 事件类
     */
    public abstract void handle(Event event);
}
