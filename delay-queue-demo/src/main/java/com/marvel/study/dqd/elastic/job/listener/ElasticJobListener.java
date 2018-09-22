/*
 * Copyright (C) 2009-2018 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package com.marvel.study.dqd.elastic.job.listener;

import com.dangdang.ddframe.job.executor.ShardingContexts;
import com.dangdang.ddframe.job.lite.api.listener.AbstractDistributeOnceElasticJobListener;
import com.marvel.study.dqd.elastic.job.dao.TaskRepository;
import com.marvel.study.dqd.elastic.job.entity.JobTask;

import javax.annotation.Resource;

/**
 * ElasticJobListener
 *
 * @author luobosi@2dfire.com
 * @since 2018-09-22
 */
public class ElasticJobListener extends AbstractDistributeOnceElasticJobListener {
    @Resource
    private TaskRepository taskRepository;

    public ElasticJobListener(long startedTimeoutMilliseconds, long completedTimeoutMilliseconds) {
        super(startedTimeoutMilliseconds, completedTimeoutMilliseconds);
    }

    @Override
    public void doBeforeJobExecutedAtLastStarted(ShardingContexts shardingContexts) {
    }

    @Override
    public void doAfterJobExecutedAtLastCompleted(ShardingContexts shardingContexts) {
        //任务执行完成后更新状态为已执行
        JobTask jobTask = taskRepository.findOne(Long.valueOf(shardingContexts.getJobParameter()));
        jobTask.setStatus(1);
        taskRepository.save(jobTask);
    }
}