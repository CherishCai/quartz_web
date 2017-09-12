/*
 * Copyright (c) caihongwen.cn 2017.
 */

package cn.cherish.learn.quartz_web.dal.entity;

import lombok.Data;
import java.util.Date;

/**
 * 定时任务配置信息
 * @author Cherish
 * @version 1.0
 * @date 2017/9/5 19:33
 */
@Data
public class TaskConfig implements java.io.Serializable {

    private static final long serialVersionUID = 1806743571890052921L;

    private Long id;
    /**
     * 任务分组
     */
    private String groupName;
    /**
     * 任务名称
     */
    private String taskName;
    /**
     * 任务状态 0禁用 1启用 2删除
     */
    private String taskStatus;
    /**
     * 任务运行时间表达式
     */
    private String taskCron;
    /**
     * 最后一次执行时间
     */
    private Date previousFireTime;
    /**
     * 下次执行时间
     */
    private Date nextFireTime;
    /**
     * 任务描述
     */
    private String taskDesc;
    // 通知邮箱地址
    private String contactName;
    private String contactEmail;

}
