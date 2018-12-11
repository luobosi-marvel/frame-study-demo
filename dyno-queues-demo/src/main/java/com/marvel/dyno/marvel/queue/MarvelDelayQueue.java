/*
 * Copyright (C) 2009-2018 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package com.marvel.dyno.marvel.queue;

import com.marvel.dyno.domain.DelayMessageDO;

import java.io.Closeable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * MarvelDelayQueue
 *
 * @author luobosi@2dfire.com
 * @since 2018-12-11
 */
public interface MarvelDelayQueue extends Closeable {
    /**
     * 返回队列的名称
     *
     * @return 返回队列的名称
     */
    String getName();

    /**
     * 弹出和未确认的消息被推回队列之前的时间（以毫秒为单位）。
     *
     * @return 弹出和未确认的消息被推回队列之前的时间（以毫秒为单位）。
     * @see #ack(String)
     */
    int getUnackTime();

    /**
     * 要推送到队列的消息
     *
     * @param messages 要推送到队列的消息
     * @return 返回消息 id
     */
    List<String> push(List<DelayMessageDO> messages);

    /**
     * @param messageCount 要从队列弹出的消息数
     * @param wait         如果队列中没有消息则等待的时间量
     * @param unit         时间单位
     * @return 如果可用消息少于消息计数，则可以小于messageCount。 如果弹出的消息未及时确认，则将它们推回队列。
     * @see #peek(int)
     * @see #ack(String)
     * @see #getUnackTime()
     */
    List<DelayMessageDO> pop(int messageCount, int wait, TimeUnit unit);

    /**
     * 提供队列，而不会将消息传出。
     *
     * @param messageCount 获取指定数量的消息(并不删除，用于预先处理)
     * @return 消息集合
     * @see #pop(int, int, TimeUnit)
     */
    List<DelayMessageDO> peek(int messageCount);

    /**
     * 提供对消息的确认。 一旦确认，消息将永远从队列中删除。
     *
     * @param messageId 确认消息的 id
     * @return 如果消息被发现等待确认并且现在已经确认，则为true。 如果消息ID无效或队列中不再存在消息，则返回false。
     */
    boolean ack(String messageId);


    /**
     * 批量确认消息
     *
     * @param messages 要确认的消息。 每条消息必须填充id和shard信息。
     */
    void ack(List<DelayMessageDO> messages);

    /**
     * Sets the unack timeout on the message (changes the default timeout to the new value).  Useful when extended lease is required for a message by consumer before sending ack.
     * 设置没有确认消息的超时时间
     *
     * @param messageId ID of the message to be acknowledged
     * @param timeout   time in milliseconds for which the message will remain in un-ack state.  If no ack is received after the timeout period has expired, the message is put back into the queue
     * @return true if the message id was found and updated with new timeout.  false otherwise.
     */
    boolean setUnackTimeout(String messageId, long timeout);


    /**
     * Updates the timeout for the message.
     * 设置消息的超时时间
     *
     * @param messageId ID of the message to be acknowledged
     * @param timeout   time in milliseconds for which the message will remain invisible and not popped out of the queue.
     * @return true if the message id was found and updated with new timeout.  false otherwise.
     */
    boolean setTimeout(String messageId, long timeout);

    /**
     * 删除某个消息
     *
     * @param messageId Remove the message from the queue
     * @return true if the message id was found and removed.  False otherwise.
     */
    boolean remove(String messageId);


    /**
     * 根据消息id 获取消息
     *
     * @param messageId 要检索的消息 id
     * @return 通过messageId检索存储在队列中的消息。 如果没有则返回空
     */
    DelayMessageDO get(String messageId);

    /**
     * 获取队列中元素的个数
     *
     * @return 返回队列大小
     * @see #shardSizes()
     */
    long size();

    /**
     * @return 将分片名称映射到分片中的消息数。
     * @see #size()
     */
    Map<String, Map<String, Long>> shardSizes();

    /**
     * 清空整个队列
     */
    void clear();
}
