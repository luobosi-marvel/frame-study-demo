/*
 * Copyright (C) 2009-2018 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package com.marvel.dyno.util;

import java.util.concurrent.TimeUnit;

/**
 * DateUtils
 * 时间工具 util
 *
 * @author luobosi@2dfire.com
 * @since 2018-12-10
 */
public class DateUtils {

    /**
     * 计算任务具体执行的时间(时间统一转换为时间戳，毫秒)
     *
     * @param referenceTime 参考时间
     * @param delay 延迟时间
     * @return 任务具体执行的时间
     */
    public static double calculationDelayTime(Long referenceTime, Integer delay, TimeUnit timeUnit) {
        return Double.parseDouble("" + (referenceTime + timeUnit.toMillis(delay)));
    }
}
