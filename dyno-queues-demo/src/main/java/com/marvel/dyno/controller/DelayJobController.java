/*
 * Copyright (C) 2009-2018 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package com.marvel.dyno.controller;

import com.marvel.dyno.domain.DelayJobDO;
import org.springframework.web.bind.annotation.*;

/**
 * DelayQueueController
 *
 * @author luobosi@2dfire.com
 * @since 2018-12-08
 */
@RestController
@RequestMapping("delay/queue")
public class DelayJobController {

    @PostMapping("add")
    public String addJob(@RequestBody DelayJobDO delayJobDO) {
        return "添加成功";
    }

    /**
     * 删除一个 job 任务
     *
     * @param id job id
     * @return 成功与否
     */
    @GetMapping("delete")
    public String deleteJob(@RequestParam("id") Long id) {
        return "删除成功";
    }



    /**
     * 完成一个任务
     *
     * @param id id
     * @return 完成一个任务
     */
    @GetMapping("finish")
    public String finishJob(@RequestParam("id") Long id) {
        return "完成一个任务";
    }
}
