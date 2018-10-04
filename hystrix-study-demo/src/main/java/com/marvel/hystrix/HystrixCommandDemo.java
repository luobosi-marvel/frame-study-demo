/*
 * Copyright (C) 2009-2018 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package com.marvel.hystrix;

import com.netflix.hystrix.*;

/**
 * HystrixCommandDemo
 *
 * @author luobosi@2dfire.com
 * @since 2018-10-04
 */
public class HystrixCommandDemo extends HystrixCommand<String> {


    public HystrixCommandDemo() {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("test"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        // 开启熔断模式
                        .withCircuitBreakerEnabled(true)
                        // 出现错误的比率超过 30% 就开启熔断
                        .withCircuitBreakerErrorThresholdPercentage(30)
                        // 至少有 10 个请求才进行 errorThresholdPercentage 错误百分比计算
                        .withCircuitBreakerRequestVolumeThreshold(10)
                        // 半开试探休眠时间，这里设置为 3 秒
                        .withExecutionTimeoutInMilliseconds(3000)
                )

        );
    }
    @Override
    protected String getFallback() {
        //当外部请求超时后，会执行fallback里的业务逻辑
        System.out.println("执行了回退方法");
        return "error";
    }


    @Override
    protected String run() throws InterruptedException {
        //模拟外部请求需要的时间长度
        System.out.println("执行了run方法");
        Thread.sleep(2000);
        return "sucess";

    }
}