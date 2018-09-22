/*
 * Copyright (C) 2009-2018 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package com.marvel.study.dqd.elastic.job.service;

import com.marvel.study.dqd.elastic.job.dao.TaskRepository;
import com.marvel.study.dqd.elastic.job.handle.ElasticJobHandler;
import com.marvel.study.dqd.elastic.job.util.CronUtils;
import com.marvel.study.dqd.elastic.job.entity.JobTask;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * ElasticJobService
 *
 * @author luobosi@2dfire.com
 * @since 2018-09-22
 */
@Service
public class ElasticJobService {
    @Resource
    private ElasticJobHandler jobHandler;
    @Resource
    private TaskRepository taskRepository;

    /**
     * 扫描db，并添加任务
     */
    public void scanAddJob() {
        Specification query = (Specification<JobTask>) (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder
                .and(criteriaBuilder.equal(root.get("status"), 0));
        List<JobTask> jobTasks = taskRepository.findAll(query);
        jobTasks.forEach(jobTask -> {
            Long current = System.currentTimeMillis();
            String jobName = "job" + jobTask.getSendTime();
            String cron;
            //说明消费未发送，但是已经过了消息的发送时间，调整时间继续执行任务
            if (jobTask.getSendTime() < current) {
                //设置为一分钟之后执行，把Date转换为cron表达式
                cron = CronUtils.getCron(new Date(current + 60000));
            } else {
                cron = CronUtils.getCron(new Date(jobTask.getSendTime()));
            }
            jobHandler.addJob(jobName, cron, 1, String.valueOf(jobTask.getId()));
        });
    }
}

