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

    private final String name;

    public HystrixCommandDemo(String name) {
//		super(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"));
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("testCommandGroupKey"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("testCommandKey"))
                /* 使用HystrixThreadPoolKey工厂定义线程池名称*/
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("testThreadPool"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
//                		.withExecutionIsolationStrategy(ExecutionIsolationStrategy.SEMAPHORE)	// 信号量隔离
                        .withExecutionTimeoutInMilliseconds(5000)));
//		HystrixCommandProperties.Setter().withCircuitBreakerEnabled(true);
//		HystrixCollapserProperties.Setter()
//		HystrixThreadPoolProperties.Setter().withCoreSize(1);
        this.name = name;
    }

//	@Override
//  protected String getFallback() {
//		System.out.println("触发了降级!");
//      return "exeucute fallback";
//  }

    @Override
    protected String run() throws InterruptedException {
//		for (int i = 0; i < 10; i++) {
//			System.out.println("runing HelloWorldHystrixCommand..." + i);
//		}
//
//		TimeUnit.MILLISECONDS.sleep(2000);
        return "Hello " + name + "! thread:" + Thread.currentThread().getName();
    }
}