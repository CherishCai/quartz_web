/*
 * Copyright (c) caihongwen.cn 2017.
 */

package cn.cherish.learn.quartz_web.dal.entity;

import lombok.Data;

import java.util.Date;

/**
 * 任务执行日志
 * @author Cherish
 * @version 1.0
 * @date 2017/9/5 20:28
 */
@Data
public class TaskLog implements java.io.Serializable {

    private static final long serialVersionUID = -731852436712131696L;

    private Long id;
    /**
     * 组别
     */
    private String groupName;
    /**
     * 任务名
     */
    private String taskName;
    /**
     * 任务开始时间
     */
    private Date startTime;
    /**
     * 任务结束时间
     */
    private Date endTime;
    /**
     * 任务状态
     */
    private String taskStatus;
    /**
     * 执行信息
     */
    private String fireInfo;
    /**
     * 机器名
     */
    private String serverHost;
    /**
     * 网卡序列号
     */
    private String serverDuid;

}