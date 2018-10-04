/*
 * Copyright (C) 2009-2018 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package com.marvel.hystrix;

/**
 * Main
 *
 * @author luobosi@2dfire.com
 * @since 2018-10-04
 */
public class Main {


    public static void main(String[] args) throws InterruptedException {

        for (int i = 0; i < 30; i++) {
        HystrixCommandDemo command = new HystrixCommandDemo();
            String result = command.execute();
            System.out.println(result);
            System.out.println("circuit Breaker is open : " + command.isCircuitBreakerOpen());
            if (command.isCircuitBreakerOpen()) {
                Thread.sleep(50);
            }
        }


    }
}
