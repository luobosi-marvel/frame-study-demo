/*
 * Copyright (C) 2009-2018 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package com.marvel.dyno.queue;

import com.marvel.dyno.domain.DelayMessageDO;
import com.marvel.dyno.marvel.queue.MarvelDelayQueue;
import com.marvel.dyno.marvel.queue.impl.MarvelRedisQueue;
import com.marvel.dyno.redis.JedisServer;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;

import java.util.List;
import java.util.stream.Collectors;

/**
 * MarvelRedisQueueTest
 *
 * @author luobosi@2dfire.com
 * @since 2018-12-11
 */
public class MarvelRedisQueueTest {

    private static JedisPool pool;

    MarvelDelayQueue delayQueue;

    private static final String queueName = "test_queue";

    private static final String redisKeyPrefix = "test-marvel-queues";

    @Before
    public void before() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setTestOnBorrow(true);
        config.setTestOnCreate(true);
        config.setMaxTotal(10);
        config.setMaxIdle(5);
        config.setMaxWaitMillis(60_000);
        pool = new JedisPool(config, "localhost", 6379);
        delayQueue = new MarvelRedisQueue(redisKeyPrefix, queueName,  6_000, pool);
    }

    @Test
    public void testJedisPipeline() {
        try (Jedis jedis = pool.getResource()) {

            Pipeline pipe = jedis.pipelined();

            for (DelayMessageDO delayMessage : getDelayMessage()) {
                double priority = delayMessage.getPriority();
                double score = Long.valueOf(delayMessage.getReferenceTime() + delayMessage.getTtr()).doubleValue() + priority;
                pipe.zadd("test-marvel-queues.QUEUE.test-queue", score, delayMessage.getId().toString());
            }
            
            pipe.sync();
            pipe.close();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    @Test
    public void testJedisPipeline1() {
        try (Jedis jedis = pool.getResource()) {

            Pipeline pipe = jedis.pipelined();

            for (int i = 0; i < 10; i++) {
                pipe.hset("marvel-hash", "marvel-" + i, "value-" + i);

                double score = Long.valueOf(System.currentTimeMillis()).doubleValue();

                pipe.zadd("marvel-zset", score, "marvel-set-" + i);
            }
            pipe.sync();
            pipe.close();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    @Test
    public void testAdd() {
        delayQueue.push(getDelayMessage()).forEach(System.out::println);
    }

    private List<DelayMessageDO> getDelayMessage() {
        List<DelayMessageDO> delayMessageDOS = Lists.newArrayList();

        for (long i = 0; i < 30L; i++) {
            DelayMessageDO delayMessageDO = new DelayMessageDO();
            delayMessageDO.setId(i);
            delayMessageDO.setReferenceTime(System.currentTimeMillis());
            final double d = Math.random();
            final int time = (int) (d * 100);
            delayMessageDO.setDelay(time);
            delayMessageDO.setTopic("order-business");
            delayMessageDO.setTtr(1L);
            delayMessageDO.setBody("{\"message\":\"some message\"}");

            delayMessageDOS.add(delayMessageDO);
        }

        return delayMessageDOS;
    }
}
