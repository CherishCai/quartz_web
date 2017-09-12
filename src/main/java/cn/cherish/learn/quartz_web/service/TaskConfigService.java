/*
 * Copyright (c) caihongwen.cn 2017.
 */

package cn.cherish.learn.quartz_web.service;

import cn.cherish.learn.quartz_web.dal.entity.TaskConfig;
import cn.cherish.learn.quartz_web.dal.mapper.TaskConfigMapper;
import cn.cherish.learn.quartz_web.quartz.SchedulerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Cherish
 * @version 1.0
 * @date 2017/9/6 14:01
 */
@Service
public class TaskConfigService {

    private final TaskConfigMapper taskConfigMapper;
    private final SchedulerManager schedulerManager;

    @Autowired
    public TaskConfigService(TaskConfigMapper taskConfigMapper, SchedulerManager schedulerManager) {
        this.taskConfigMapper = taskConfigMapper;
        this.schedulerManager = schedulerManager;
    }

    @Transactional(readOnly = true)
    public List<TaskConfig> findAll(){
        return taskConfigMapper.findAll();
    }

    @Transactional(readOnly = true)
    public void scheduleAllTask() {
        List<TaskConfig> taskConfig = taskConfigMapper.findAll();
        taskConfig.forEach(schedulerManager::addOrUpdateTask);
    }

}
