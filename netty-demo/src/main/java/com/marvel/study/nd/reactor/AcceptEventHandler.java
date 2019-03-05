/*
 * Copyright (C) 2009-2018 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package com.marvel.study.nd.reactor;

/**
 * AcceptEventHandler
 * accept 事件处理类
 *
 * @author luobosi@2dfire.com
 * @since 2019-01-21
 */
public class AcceptEventHandler extends EventHandler {

    private Selector selector;

    public AcceptEventHandler(Selector selector) {
        this.selector = selector;
    }

    @Override
    public void handle(Event event) {
        //处理Accept的event事件
        if (event.getType() == EventType.ACCEPT) {

            //TODO 处理ACCEPT状态的事件

            //将事件状态改为下一个READ状态，并放入selector的缓冲队列中
            Event readEvent = new Event();
            readEvent.setInputSource(event.getInputSource());
            readEvent.setType(EventType.READ);

            selector.addEvent(readEvent);
        }
    }

}
