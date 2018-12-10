/*
 * Copyright (C) 2009-2018 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package com.marvel.dyno.controller;

import com.alibaba.fastjson.JSON;
import com.marvel.dyno.domain.DelayJobDO;
import com.marvel.dyno.redis.RedisService;
import com.marvel.dyno.util.DateUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * DelayQueueController
 *
 * @author luobosi@2dfire.com
 * @since 2018-12-08
 */
@RestController
@RequestMapping("delay/queue")
public class DelayJobController {

    @Resource
    private RedisService redisService;

    @PostMapping("add")
    public DelayJobDO addJob(@RequestBody DelayJobDO delayJobDO) {

        redisService.zadd(
                this.getJobKey(delayJobDO.getTopic()),
                DateUtils.calculationDelayTime(delayJobDO.getReferenceTime(), delayJobDO.getDelay(), TimeUnit.SECONDS),
                this.getValue(delayJobDO.getId()));

        redisService.hset(getJobHashKey(delayJobDO.getTopic(), delayJobDO.getId()), this.getValue(delayJobDO.getId()), delayJobDO.toString(), -1);

        List<String> jsonString = redisService.hmget(getJobHashKey(delayJobDO.getTopic(), delayJobDO.getId()), Collections.singletonList("" + delayJobDO.getId()));

        return JSON.parseObject(jsonString.get(0), DelayJobDO.class);
    }



    /**
     * 删除一个 job 任务
     *
     * @param id job id
     * @return 成功与否
     */
    @GetMapping("delete")
    public String deleteJob(@RequestParam("id") Long id) {
        return "删除成功";
    }



    /**
     * 完成一个任务
     *
     * @param id id
     * @return 完成一个任务
     */
    @GetMapping("finish")
    public String finishJob(@RequestParam("id") Long id) {
        return "完成一个任务";
    }

    /**
     * 根据 topic 和 id 拼装任务执行的 key
     *
     * @param topic 主题
     * @return job key
     */
    private String getJobHashKey(String topic, Long id) {
        return topic + ":" + id;
    }

    /**
     * 根据 topic 和 id 拼装任务执行的 key
     *
     * @param topic 主题
     * @return job key
     */
    private String getJobKey(String topic) {
        return topic;
    }

    /**
     * 有序集合里面存储的 value
     *
     * @param id id
     * @return 返回 value
     */
    private String getValue(Long id) {
        return String.valueOf(id);
    }
}
