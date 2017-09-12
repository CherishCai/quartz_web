/*
 * Copyright (c) caihongwen.cn 2017.
 */

package cn.cherish.learn.quartz_web.service;

import cn.cherish.learn.quartz_web.dal.entity.TaskConfig;
import cn.cherish.learn.quartz_web.quartz.SchedulerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Cherish
 * @version 1.0
 * @date 2017/9/5 21:14
 */
@Service
public class SchedulerService {

    private final SchedulerManager schedulerManager;

    @Autowired
    public SchedulerService(SchedulerManager schedulerManager) {
        this.schedulerManager = schedulerManager;
    }

    /**
     * 修改任务
      * @param taskConfig
     */ 
    public void addOrUpdateTask(TaskConfig taskConfig) {
        schedulerManager.addOrUpdateTask(taskConfig);
    }
    
    // 获取所有作业
    public List<TaskConfig> getAllTaskDetail() {
        return schedulerManager.getAllJobDetail();
    }

    // 执行作业
    public void execTask(TaskConfig taskConfig) {
        schedulerManager.runJob(taskConfig);
    }

    // 恢复作业
    public void openTask(TaskConfig taskConfig) {
        schedulerManager.resumeJob(taskConfig);
    }

    // 暂停作业
    public void closeTask(TaskConfig taskConfig) {
        schedulerManager.stopJob(taskConfig);
    }

    // 删除作业
    public void delTask(TaskConfig taskConfig) {
        schedulerManager.delJob(taskConfig);
    }


}
