/*
 * Copyright (C) 2009-2018 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package com.marvel.study.nd.reactor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Selector
 * reactor 模式中 Domultiplexer 类，提供select（）方法用于从缓冲队列中查找出符合条件的event列表
 *
 * @author luobosi@2dfire.com
 * @since 2019-01-21
 */
public class Selector {

    /**
     * 定义一个链表阻塞queue实现缓冲队列，用于保证线程安全
     */
    private BlockingQueue<Event> eventQueue = new LinkedBlockingQueue<>();

    /**
     * 定义一个object用于synchronize方法块上锁
     */
    private Object lock = new Object();

    public List<Event> select() {
        return select(0);
    }

    public List<Event> select(long timeout) {
        if (timeout > 0) {
            if (eventQueue.isEmpty()) {
                synchronized (lock) {
                    if (eventQueue.isEmpty()) {
                        try {
                            lock.wait(timeout);
                        } catch (InterruptedException e) {
                            System.out.println("错误打印");
                        }
                    }
                }
            }
        }

        //TODO 例子中只是简单的将event列表全部返回，可以在此处增加业务逻辑，选出符合条件的event进行返回
        List<Event> events = new ArrayList<>();
        eventQueue.drainTo(events);
        return events;
    }

    /**
     * 新增一个事件
     *
     * @param e 事件
     */
    public void addEvent(Event e) {
        //将event事件加入队列
        boolean success = eventQueue.offer(e);
        if (success) {
            synchronized (lock) {
                //如果有新增事件则对lock对象解锁
                lock.notify();
            }

        }
    }
}
