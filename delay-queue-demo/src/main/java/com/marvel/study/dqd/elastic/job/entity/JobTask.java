package com.marvel.study.dqd.elastic.job.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * MyElasticJob
 *
 * @author luobosi@2dfire.com
 * @since 2018-09-22
 */
@Entity
@Table(name = "JOB_TASK")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobTask {
    @Id
    @GeneratedValue
    private Long id;
    @Column
    private String content;
    /**
     * 0-未执行
     * 1-已执行
     */
    @Column
    private Integer status;
    @Column(name = "send_time")
    private Long sendTime;

}