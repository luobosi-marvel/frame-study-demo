/*
 * Copyright (C) 2009-2018 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package com.marvel.study.nd.reactor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Dispatcher
 * reactor模式中Dispatcher类，负责event的分发和eventHandler的维护
 *
 * @author luobosi@2dfire.com
 * @since 2019-01-21
 */
public class Dispatcher {

    /**
     * 通过ConcurrentHashMap来维护不同事件处理器
     */
    Map<EventType, EventHandler> eventHandlerMap = new ConcurrentHashMap<EventType, EventHandler>();
    /**
     * 本例只维护一个selector负责事件选择，netty为了保证性能实现了多个selector来保证循环处理性能，不同事件加入不同的selector的事件缓冲队列
     */
    private Selector selector;

    public Dispatcher(Selector selector) {
        this.selector = selector;
    }

    //在Dispatcher中注册eventHandler
    public void registEventHandler(EventType eventType, EventHandler eventHandler) {
        eventHandlerMap.put(eventType, eventHandler);

    }

    public void removeEventHandler(EventType eventType) {
        eventHandlerMap.remove(eventType);
    }

    public void handleEvents() {
        dispatch();
    }

    /**
     * 此例只是实现了简单的事件分发给相应的处理器处理，例子中的处理器都是同步，在reactor模式的典型实现NIO中都是在handle异步处理，来保证非阻塞
     */
    private void dispatch() {
        while (true) {
            List<Event> events = selector.select();
            // 循环处理这里是线性的
            // 可以采用 RxJava 改造成并行的
            for (Event event : events) {
                EventHandler eventHandler = eventHandlerMap.get(event.getType());
                eventHandler.handle(event);
            }
        }
    }
}
