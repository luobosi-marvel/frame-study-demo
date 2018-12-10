/*
 * Copyright (C) 2009-2018 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package com.marvel.dyno.domain;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

/**
 * DelayJobDO
 * 延迟队列 入参实体
 *
 * @author luobosi@2dfire.com
 * @since 2018-12-08
 */
@Data
public class DelayJobDO implements Serializable {

    /**
     * Job唯一标识
     */
    private Long id;

    /**
     * Job类型
     */
    private String topic;

    /**
     * 参考时间，在该时间基础下延迟指定时间
     */
    private Long referenceTime;

    /**
     * Job需要延迟的时间, 单位：秒
     */
    private Integer delay;

    /**
     * Job执行超时时间, 单位：秒
     */
    private Integer ttr;

    /**
     * Job的内容，供消费者做具体的业务处理，如果是json格式需转义
     */
    private String body;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

}
