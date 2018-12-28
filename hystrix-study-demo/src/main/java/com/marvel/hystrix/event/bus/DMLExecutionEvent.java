/*
 * Copyright (C) 2009-2018 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package com.marvel.hystrix.event.bus;

import lombok.Data;

import java.util.Date;

/**
 * DMLExecutionEvent
 *
 * @author luobosi@2dfire.com
 * @since 2018-12-28
 */
@Data
public class DMLExecutionEvent {
    private String id;
    private String dataSource;
    private Date sendTime;
}
