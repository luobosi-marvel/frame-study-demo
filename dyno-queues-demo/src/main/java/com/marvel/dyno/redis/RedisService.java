/*
 * Copyright (C) 2009-2018 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package com.marvel.dyno.redis;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * RedisService
 *
 * @author luobosi@2dfire.com
 * @since 2018-12-08
 */
@Component
public class RedisService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    // 默认过期秒数
    private static final int DEFAULT_EXPIRE_SECOND = -1;

    /**
     * 设置一个 key
     *
     * @param key   key
     * @param value value
     */
    public void set(String key, String value) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key, value);
    }

    /**
     * 存储 object
     *
     * @param key   key
     * @param value value
     */
    public void setObject(String key, Object value) {
        setObject(key, value, DEFAULT_EXPIRE_SECOND);
    }

    /**
     * 存储 object
     *
     * @param key          key
     * @param value        value
     * @param expireSecond 过期秒数
     */
    public void setObject(String key, Object value, int expireSecond) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key, value, expireSecond, TimeUnit.SECONDS);
    }

    /**
     * 通过 key 获取一个 对象
     *
     * @param key key
     * @return Object
     */
    public Object getObjet(String key) {
        return getObject(key);
    }

    /**
     * 返回obj
     *
     * @param key key
     * @return 对象
     */
    public Object getObject(String key) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        return valueOperations.get(key);
    }

    ///////////////////////////////////// hash 操作 /////////////////////////////////////

    /**
     * 设置hash 的某个值
     *
     * @param key
     * @param field
     * @param value
     * @param expireSecond
     * @return
     */
    public void hset(String key, String field, String value, int expireSecond) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();

        hashOperations.put(key, field, value);
        if (expireSecond != -1) {
            redisTemplate.expire(key, expireSecond, TimeUnit.SECONDS);
        }
    }

    /**
     * 将哈希表 key 中的域 field 的值设置为 value ，当且仅当域 field 不存在。
     * 若域 field 已经存在，该操作无效
     *
     * @param key
     * @param field
     * @param value
     * @param expireSecond
     * @return 设置成功，返回 1 。如果给定域已经存在且没有操作被执行，返回 0 。
     */
    public boolean hsetnx(String key, String field, String value, int expireSecond) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();

        boolean flag = hashOperations.putIfAbsent(key, field, value);
        if (flag && expireSecond != -1) {
            redisTemplate.expire(key, expireSecond, TimeUnit.SECONDS);
        }
        return flag;
    }

    /**
     * set hash map
     *
     * @param key
     * @param hash
     * @param expireSecond 过期秒数
     */
    public void hmset(String key, Map<String, String> hash, int expireSecond) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        hashOperations.putAll(key, hash);
        if (expireSecond != -1) {
            redisTemplate.expire(key, expireSecond, TimeUnit.SECONDS);
        }
    }


    /**
     * 删除hash 中的某个field
     *
     * @param key
     * @param fields
     * @return
     */
    public long hdel(String key, String... fields) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        return hashOperations.delete(key, fields);
    }


    /**
     * 返回多个hash value
     *
     * @param key
     * @param fields
     * @return
     */
    public List<String> hmget(String key, List<String> fields) {

        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        return hashOperations.multiGet(key, fields);
    }

    /**
     * 返回指定hash的field数量
     *
     * @param key
     * @return
     */
    public Long hlen(String key) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        return hashOperations.size(key);
    }

    /**
     * 查看哈希表 key 中，给定域 field 是否存在。
     *
     * @param key
     * @param field
     * @return 如果哈希表含有给定域，返回 true 。如果哈希表不含有给定域，或 key 不存在，返回 false 。
     */
    public Boolean hexists(String key, String field) {
        return false;
    }

    /**
     * 返回哈希表 key 中的所有域。
     *
     * @param key
     * @return
     */
    public Set<String> hkeys(String key) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        return hashOperations.keys(key);
    }


    ///////////////////////////////////// 有序集合 /////////////////////////////////////

    /**
     * 有序存储object
     *
     * @param key   key
     * @param score 分值
     * @param value value
     * @return 1:成功 0:不成功
     */
    public boolean zadd(String key, double score, Object value) {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        return zSetOperations.add(key, value, score);
    }

    /**
     * 获取有序存储object的score值
     *
     * @param key   key
     * @param value value
     * @return score值
     */
    public double zscore(String key, String value) {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        return zSetOperations.score(key, value);
    }

    /**
     * 删除对应value值
     *
     * @param key   key
     * @param value value
     * @return 删除影响的条数
     */
    public Long zrem(String key, Object... value) {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        return zSetOperations.remove(key, value);
    }

    public Set<Object> zrange(String key, long start, long end, int isAsc) {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        return zSetOperations.range(key, start, end);
    }

    /**
     * 返回有序集 key 中成员 member 的排名。
     *
     * @param key    key
     * @param member filed
     * @return 排名
     */
    public long zrank(String key, Object member) {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        Long pos = zSetOperations.rank(key, member);
        return pos == null ? 0 : pos;
    }

    /**
     * 返回有序集 key 的基数
     *
     * @param key 键值
     * @return 基数
     */
    public long zcard(String key) {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        Long pos = zSetOperations.zCard(key);
        return pos == null ? 0 : pos;
    }

    /**
     * 移除有序集 key 中，指定排名(rank)区间内的所有成员。
     * <p>
     * 区间分别以下标参数 start 和 stop 指出，包含 start 和 stop 在内。
     * <p>
     * 下标参数 start 和 stop 都以 0 为底，也就是说，以 0 表示有序集第一个成员，以 1 表示有序集第二个成员，以此类推。
     * 你也可以使用负数下标，以 -1 表示最后一个成员， -2 表示倒数第二个成员，以此类推。
     *
     * @param key   key
     * @param start 起始位置
     * @param end   结束位置
     * @return 影响的元素个数
     */
    public long zremrangebyrank(String key, long start, long end) {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        Long pos = zSetOperations.removeRange(key, start, end);
        return pos == null ? 0 : pos;
    }


    /**
     * 返回有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。有序集成员按 score 值递增(从小到大)次序排列。
     * <p>
     * 具有相同 score 值的成员按字典序(lexicographical order)来排列(该属性是有序集提供的，不需要额外的计算)。
     * <p>
     * 可选的 LIMIT 参数指定返回结果的数量及区间(就像SQL中的 SELECT LIMIT offset, count )，注意当 offset 很大时，定位 offset 的操作可能需要遍历整个有序集，此过程最坏复杂度为 O(N) 时间。
     * <p>
     * 可选的 WITHSCORES 参数决定结果集是单单返回有序集的成员，还是将有序集成员及其 score 值一起返回。
     *
     * @param key key
     * @param max 最大值
     * @param min 最小值
     * @return
     */
    public Set<Object> zrangeByScore(final String key, final double min, final double max) {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        return zSetOperations.rangeByScore(key, min, max);
    }

    /**
     * 返回有序集 key 中， score 值介于 max 和 min 之间(默认包括等于 max 或 min )的所有的成员。有序集成员按 score 值递减(从大到小)的次序排列。
     * <p>
     * 具有相同 score 值的成员按字典序的逆序(reverse lexicographical order )排列。
     * <p>
     * 除了成员按 score 值递减的次序排列这一点外， ZREVRANGEBYSCORE 命令的其他方面和 ZRANGEBYSCORE 命令一样。
     *
     * @param key key
     * @param max 最大值
     * @param min 最小值
     * @return 范围内的元素 反序
     */
    public Set<Object> zrevrangeByScore(final String key, final double max, final double min) {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        return zSetOperations.reverseRangeByScore(key, min, max);
    }

    /**
     * 返回有序集 key 中， score 值在 min 和 max 之间(默认包括 score 值等于 min 或 max )的成员的数量。
     *
     * @param key key
     * @param min 最小分数值
     * @param max 最多分数值
     * @return 范围内的数量
     */
    public Long zcount(final String key, final double min, final double max) {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        return zSetOperations.count(key, min, max);
    }
}
