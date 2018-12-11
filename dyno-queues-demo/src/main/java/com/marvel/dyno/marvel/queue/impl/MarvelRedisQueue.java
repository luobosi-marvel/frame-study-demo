/*
 * Copyright (C) 2009-2018 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package com.marvel.dyno.marvel.queue.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.marvel.dyno.domain.DelayMessageDO;
import com.marvel.dyno.marvel.queue.MarvelDelayQueue;
import com.netflix.dyno.connectionpool.HashPartitioner;
import com.netflix.dyno.connectionpool.impl.hash.Murmur3HashPartitioner;
import redis.clients.jedis.*;
import redis.clients.jedis.params.sortedset.ZAddParams;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * MarvelRedisQueue
 * 延迟队列实现1
 *
 * @author luobosi@2dfire.com
 * @since 2018-12-11
 */
public class MarvelRedisQueue implements MarvelDelayQueue {

    /**
     * 队列名称
     */
    private String queueName;

    /**
     * 消息存储的前缀
     */
    private String messageStoreKeyPrefix;

    /**
     * 这里理解成我们的 topic 即可
     */
    private String myQueueShard;

    /**
     * 没有确认队列的前缀信息
     */
    private String unackShardKeyPrefix;

    /**
     * 没有确认的时间，超过了这个时间则继续放入 topic 队列中去
     */
    private int unackTime = 60;

    /**
     * 在内存中存储对象信息
     */
    private ObjectMapper om;

    private JedisPool connPool;

    private JedisPool nonQuorumPool;

    /**
     * 周期性检测 unack
     */
    private ScheduledExecutorService schedulerForUnacksProcessing;

    /**
     * 周期性预先加载 redis 有序集合里面的数据
     */
    private ScheduledExecutorService schedulerForPrefetchProcessing;

    /**
     * 用来计算 hash 值
     */
    private HashPartitioner partitioner = new Murmur3HashPartitioner();

    /**
     * 最大 hash 桶的个数
     */
    private int maxHashBuckets = 1024;

    public MarvelRedisQueue(String redisKeyPrefix, String queueName, int unackTime, JedisPool pool) {
        this(redisKeyPrefix, queueName, unackTime, unackTime, pool);
    }

    public MarvelRedisQueue(String redisKeyPrefix, String queueName, int unackScheduleInMS, int unackTime, JedisPool pool) {
        this.queueName = queueName;
        this.messageStoreKeyPrefix = redisKeyPrefix + ".MESSAGE.";
        this.myQueueShard = redisKeyPrefix + ".QUEUE." + queueName;
        this.unackShardKeyPrefix = redisKeyPrefix + ".UNACK." + queueName;
        this.unackTime = unackTime;
        this.connPool = pool;
        this.nonQuorumPool = pool;

        ObjectMapper om = new ObjectMapper();
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        om.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        om.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        om.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        om.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        om.disable(SerializationFeature.INDENT_OUTPUT);

        this.om = om;

        schedulerForUnacksProcessing = Executors.newScheduledThreadPool(1);
        schedulerForPrefetchProcessing = Executors.newScheduledThreadPool(1);

        schedulerForUnacksProcessing.scheduleAtFixedRate(this::processUnacks, unackScheduleInMS, unackScheduleInMS, TimeUnit.MILLISECONDS);

    }

    public void setNonQuorumPool(JedisPool nonQuorumPool) {
        this.nonQuorumPool = nonQuorumPool;
    }

    @Override
    public String getName() {
        return this.queueName;
    }

    @Override
    public int getUnackTime() {
        return this.unackTime;
    }

    @Override
    public List<String> push(List<DelayMessageDO> messages) {
        System.out.println("myQueueShard: " + myQueueShard);
        try (Jedis jedis = connPool.getResource()) {

            Pipeline pipe = jedis.pipelined();

            for (DelayMessageDO delayMessageDO : messages) {
                String json = om.writeValueAsString(delayMessageDO);
                pipe.hset(messageStoreKey(delayMessageDO.getId().toString()), delayMessageDO.getId().toString(), json);

                double priority = delayMessageDO.getPriority();
                double score = Long.valueOf(delayMessageDO.getReferenceTime() + delayMessageDO.getTtr()).doubleValue() + priority;

                pipe.zadd(myQueueShard, score, delayMessageDO.getId().toString());
            }
            pipe.sync();
            pipe.close();

            return messages.stream().map(delayMessageDO -> delayMessageDO.getId().toString()).collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    @Override
    public List<DelayMessageDO> pop(int messageCount, int wait, TimeUnit unit) {

        if (messageCount < 1) {
            return Collections.emptyList();
        }

        try {

            List<String> peeked = new ArrayList<>(peekIds(0, messageCount));
            return _pop(peeked);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<DelayMessageDO> peek(int messageCount) {
        if (messageCount < 1) {
            return Collections.emptyList();
        }

        try {

            List<String> peeked = new ArrayList<>(peekIds(0, messageCount));
            return _pop(peeked);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Set<String> peekIds(int offset, int count) {
        try (Jedis jedis = connPool.getResource()) {
            double now = Long.valueOf(System.currentTimeMillis() + 1).doubleValue();
            // 返回有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。有序集成员按 score 值递增(从小到大)次序排列。
            return jedis.zrangeByScore(myQueueShard, 0, now, offset, count);
        }
    }

    private List<DelayMessageDO> _pop(List<String> batch) throws Exception {

        double unackScore = Long.valueOf(System.currentTimeMillis() + unackTime).doubleValue();

        List<DelayMessageDO> popped = new LinkedList<>();
        ZAddParams zParams = ZAddParams.zAddParams().nx();

        try (Jedis jedis = connPool.getResource()) {

            Pipeline pipe = jedis.pipelined();

            // 从有序集合中拿到准备好的任务
            List<Response<Long>> zadds = new ArrayList<>(batch.size());
            for (String msgId : batch) {
                if (msgId == null) {
                    break;
                }
                zadds.add(pipe.zadd(unackShardKey(msgId), unackScore, msgId, zParams));
            }
            pipe.sync();

            // 删除有序集合中的元素
            int count = zadds.size();
            List<String> zremIds = new ArrayList<>(count);
            List<Response<Long>> zremRes = new LinkedList<>();
            for (int i = 0; i < count; i++) {
                long added = zadds.get(i).get();
                if (added == 0) {
                    // todo 日志
                    continue;
                }
                String id = batch.get(i);
                zremIds.add(id);
                zremRes.add(pipe.zrem(myQueueShard, id));
            }
            pipe.sync();

            // 从 hash 表中拿到元数据信息
            List<Response<String>> getRes = new ArrayList<>(count);
            for (int i = 0; i < zremRes.size(); i++) {
                long removed = zremRes.get(i).get();
                if (removed == 0) {
                    // todo：日志
                    continue;
                }
                getRes.add(pipe.hget(messageStoreKey(zremIds.get(i)), zremIds.get(i)));
            }
            pipe.sync();

            // 解析元数据
            for (Response<String> getRe : getRes) {
                String json = getRe.get();
                if (json == null) {
                    // todo：日志
                    continue;
                }
                DelayMessageDO msg = om.readValue(json, DelayMessageDO.class);
                popped.add(msg);
            }
            return popped;
        }
    }

    @Override
    public boolean ack(String messageId) {
        try (Jedis jedis = connPool.getResource()) {

            Long removed = jedis.zrem(unackShardKey(messageId), messageId);
            if (removed > 0) {
                jedis.hdel(messageStoreKey(messageId), messageId);
                return true;
            }

            return false;

        }
    }

    @Override
    public void ack(List<DelayMessageDO> messages) {
        Jedis jedis = connPool.getResource();
        Pipeline pipe = jedis.pipelined();
        List<Response<Long>> responses = new LinkedList<>();
        try {
            for (DelayMessageDO msg : messages) {
                // 将信息从 unack 中删除
                responses.add(pipe.zrem(unackShardKey(msg.getId().toString()), msg.getId().toString()));
            }
            pipe.sync();
            pipe.close();

            List<Response<Long>> dels = new LinkedList<>();
            for (int i = 0; i < messages.size(); i++) {
                Long removed = responses.get(i).get();
                if (removed > 0) {
                    // todo: 删除信息元数据
                    dels.add(pipe.hdel(messageStoreKey(messages.get(i).getId().toString()), messages.get(i).getId().toString()));
                }
            }
            pipe.sync();
            pipe.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            jedis.close();
        }
    }

    @Override
    public boolean setUnackTimeout(String messageId, long timeout) {

        try (Jedis jedis = connPool.getResource()) {

            double unackScore = Long.valueOf(System.currentTimeMillis() + timeout).doubleValue();
            Double score = jedis.zscore(unackShardKey(messageId), messageId);
            if (score != null) {
                jedis.zadd(unackShardKey(messageId), unackScore, messageId);
                return true;
            }

            return false;

        }
    }

    @Override
    public boolean setTimeout(String messageId, long timeout) {

        try (Jedis jedis = connPool.getResource()) {
            String json = jedis.hget(messageStoreKey(messageId), messageId);
            if (json == null) {
                return false;
            }
            DelayMessageDO message = om.readValue(json, DelayMessageDO.class);
            message.setTtr(timeout);

            Double score = jedis.zscore(myQueueShard, messageId);
            if (score != null) {
                double priorityd = message.getPriority() / 100.0;
                double newScore = Long.valueOf(System.currentTimeMillis() + timeout).doubleValue() + priorityd;
                jedis.zadd(myQueueShard, newScore, messageId);
                json = om.writeValueAsString(message);
                jedis.hset(messageStoreKey(message.getId().toString()), message.getId().toString(), json);
                return true;

            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean remove(String messageId) {

        try (Jedis jedis = connPool.getResource()) {
            jedis.zrem(unackShardKey(messageId), messageId);
            Long removed = jedis.zrem(myQueueShard, messageId);
            Long msgRemoved = jedis.hdel(messageStoreKey(messageId), messageId);
            return removed > 0 && msgRemoved > 0;
        }
    }

    @Override
    public DelayMessageDO get(String messageId) {

        try (Jedis jedis = connPool.getResource()) {
            String json = jedis.hget(messageStoreKey(messageId), messageId);
            if (json == null) {
                // todo: 日志
                return null;
            }
            return om.readValue(json, DelayMessageDO.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long size() {

        try (Jedis jedis = nonQuorumPool.getResource()) {
            return jedis.zcard(myQueueShard);
        }
    }

    @Override
    public Map<String, Map<String, Long>> shardSizes() {
        return null;
    }

    @Override
    public void clear() {

    }

    private void processUnacks() {
        for (int i = 0; i < maxHashBuckets; i++) {
            String unackShardKey = unackShardKeyPrefix + i;
            processUnacks(unackShardKey);
        }
    }

    private void processUnacks(String unackShardKey) {

        try (Jedis jedis = connPool.getResource()) {
            do {

                int batchSize = 1_000;

                double now = Long.valueOf(System.currentTimeMillis()).doubleValue();
                // 返回有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。有序集成员按 score 值递增(从小到大)次序排列
                Set<Tuple> unacks = jedis.zrangeByScoreWithScores(unackShardKey, 0, now, 0, batchSize);

                if (unacks.size() > 0) {
                    // 打印日志
                } else {
                    return;
                }

                for (Tuple unack : unacks) {
                    double score = unack.getScore();
                    String member = unack.getElement();

                    String payload = jedis.hget(messageStoreKey(member), member);
                    if (payload == null) {
                        jedis.zrem(unackShardKey(member), member);
                        continue;
                    }

                    jedis.zadd(myQueueShard, score, member);
                    jedis.zrem(unackShardKey(member), member);
                }

            } while (true);
        }
    }

    /**
     * 计算源节点存储的信息（将消息放到指定桶中去）
     *
     * @param msgId 消息id
     * @return 计算之后的 hash key
     */
    private String messageStoreKey(String msgId) {
        Long hash = partitioner.hash(msgId);
        long bucket = hash % maxHashBuckets;
        return messageStoreKeyPrefix + bucket + "." + queueName;
    }

    /**
     * 这里应该是没有确认的消息
     *
     * @param messageId
     * @return
     */
    private String unackShardKey(String messageId) {
        Long hash = partitioner.hash(messageId);
        long bucket = hash % maxHashBuckets;
        return unackShardKeyPrefix + bucket;
    }

    @Override
    public void close() throws IOException {
        schedulerForUnacksProcessing.shutdown();
        schedulerForPrefetchProcessing.shutdown();
    }
}
