/*
 * Copyright (C) 2009-2018 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package com.marvel.study.dqd.elastic.job.dao;

import com.marvel.study.dqd.elastic.job.entity.JobTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * TaskRepository
 *
 * @author luobosi@2dfire.com
 * @since 2018-09-22
 */
public interface TaskRepository extends JpaRepository<JobTask, Long>, JpaSpecificationExecutor<JobTask> {

    JobTask findOne(Long valueOf);
}


