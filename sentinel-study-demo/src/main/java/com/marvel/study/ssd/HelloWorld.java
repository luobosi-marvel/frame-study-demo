/*
 * Copyright (C) 2009-2018 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package com.marvel.study.ssd;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;

import java.util.ArrayList;
import java.util.List;

/**
 * HelloWorld
 *
 * @author luobosi@2dfire.com
 * @since 2018-10-13
 */
public class HelloWorld {
    public static void main(String[] args) throws InterruptedException {
        initFlowRules();
        int i = 0;
        do {
            Entry entry = null;
            try {
                entry = SphU.entry("HelloWorld");
                System.out.println("hello world");
                // 注意，当 i == 19 的时候，即已经满足 20 个请求了，那么再次请求就会抛出 BlockException 异常，所以阻塞一秒就不会抛出异常
                // 但是这里如果改成 i == 20 那么就会 block，且之后不会走 if (i == 20) 这个逻辑，如果需要抛出一个 block 之后阻塞 1s ，需要在 catch 里面做
                if (i == 19) {
                    System.out.println("开始阻塞");
                    Thread.sleep(1000);
                }
            } catch (BlockException e1) {
                System.out.println("block!");
                if (i == 20) {
                    System.out.println("开始阻塞");
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                if (entry != null) {
                    entry.exit();
                }
            }
            i++;
        } while (i < 30);
    }

    /**
     * 定义规则
     */
    private static void initFlowRules() {
        List<FlowRule> rules = new ArrayList<>();
        FlowRule rule = new FlowRule();
        rule.setResource("HelloWorld");
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        // Set limit QPS to 20. 设置请求流量为 20
        rule.setCount(20);
        rules.add(rule);
        FlowRuleManager.loadRules(rules);
    }

}
