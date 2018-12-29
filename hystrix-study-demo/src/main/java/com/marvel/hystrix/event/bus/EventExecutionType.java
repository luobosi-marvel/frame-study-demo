/*
 * Copyright (C) 2009-2018 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package com.marvel.hystrix.event.bus;

/**
 * EventExecutionType
 *
 * @author luobosi@2dfire.com
 * @since 2018-12-28
 */
public enum  EventExecutionType {
    /**
     * Before SQL execute.
     *
     * SQL 执行之前的事件
     */
    BEFORE_EXECUTE,

    /**
     * SQL execute success.
     *
     * SQL 执行成功事件
     */
    EXECUTE_SUCCESS,

    /**
     * SQL execute failure.
     *
     * SQL 执行失败事件
     */
    EXECUTE_FAILURE
}
