/*
 * Copyright (C) 2009-2018 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package com.marvel.study.dqd.elastic.job.task;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import lombok.extern.slf4j.Slf4j;

/**
 * MyElasticJob
 *
 * @author luobosi@2dfire.com
 * @since 2018-09-22
 */
@Slf4j
public class MyElasticJob implements SimpleJob {
    @Override
    public void execute(ShardingContext shardingContext) {
        //打印出任务相关信息，JobParameter用于传递任务的ID
        log.info("任务名：{}, 片数：{}, id={}", shardingContext.getJobName(), shardingContext.getShardingTotalCount(),
                shardingContext.getJobParameter());
    }
}
