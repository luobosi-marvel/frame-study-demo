/*
 * Copyright (C) 2009-2018 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package com.marvel.study.nd.reactor;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * InputSource
 *
 * @author luobosi@2dfire.com
 * @since 2019-01-21
 */
@Data
@ToString
public class InputSource<T> implements Serializable {
    /**
     * 具体的执行数据
     */
    private T data;

    /**
     * 主键id（唯一标示）
     */
    private Long id;
}
