/*
 * Copyright (C) 2009-2018 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package com.marvel.fsd;

import com.greenpineyu.fel.FelEngine;
import com.greenpineyu.fel.FelEngineImpl;
import com.greenpineyu.fel.context.FelContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Main
 *
 * @author luobosi@2dfire.com
 * @since 2019-01-22
 */
public class Main {

    public static void main(String[] args) {
        FelEngine fel = new FelEngineImpl();
        Object result = fel.eval("5000 * 12 + 7500");
        System.out.println(result);

        System.out.println("===========");
        test1();

        System.out.println("===========");
        test2();
        System.out.println("===========");
        test3();
    }

    /**
     * 变量
     */
    private static void test1() {
        FelEngine fel = new FelEngineImpl();
        FelContext context = fel.getContext();
        context.set("单价", 5000);
        context.set("数量", 12);
        context.set("运费", 7500);
        Object result = fel.eval("单价 + 数量 + 运费");
        System.out.println(result);
    }

    /**
     * 调用 JAVA 方法
     */
    private static void test2() {
        FelEngineImpl engine = new FelEngineImpl();
        FelContext context = engine.getContext();
        context.set("out", System.out);
        engine.eval("out.println('Hello Everybody'.substring(6))");
    }

    /**
     * 访问对象属性
     * <p>
     * 在Fel中，可能非常方便的访问对象属性，示例代码如下所示
     */
    private static void test3() {

        FelEngine fel = new FelEngineImpl();
        FelContext ctx = fel.getContext();
        Foo foo = new Foo();
        ctx.set("foo", foo);
        Map<String, String> m = new HashMap<>();
        m.put("ElName", "fel");
        ctx.set("m", m);

        //调用foo.getSize()方法。
        Object result = fel.eval("foo.size");
        System.out.println(result);
        //调用foo.isSample()方法。
        result = fel.eval("foo.sample");
        System.out.println(result);
        //foo没有name、getName、isName方法
        //foo.name会调用foo.get("name")方法。
        result = fel.eval("foo.name");
        System.out.println(result);
        //m.ElName会调用m.get("ElName");
    }


}
