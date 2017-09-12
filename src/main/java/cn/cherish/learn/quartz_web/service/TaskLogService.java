/*
 * Copyright (c) caihongwen.cn 2017.
 */

package cn.cherish.learn.quartz_web.service;

import cn.cherish.learn.quartz_web.dal.entity.TaskLog;
import cn.cherish.learn.quartz_web.dal.mapper.TaskLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Cherish
 * @version 1.0
 * @date 2017/9/6 14:35
 */
@Slf4j
@Service
public class TaskLogService {

    private final TaskLogMapper logMapper;

    @Autowired
    public TaskLogService(TaskLogMapper logMapper) {
        this.logMapper = logMapper;
    }

    @Transactional(readOnly = true)
    public TaskLog getLogById(Long id) {
        return logMapper.findById(id);
    }

    @Transactional
    public TaskLog updateLog(TaskLog taskLog) {
        log.info("taskLog : {}", taskLog);
        if (taskLog == null) {
            return null;
        }
        if (taskLog.getId() == null) {
            logMapper.insert(taskLog);
        } else {
            logMapper.updateById(taskLog);
        }
        return taskLog;
    }

    @Transactional
    public TaskLog insert(TaskLog taskLog) {
        log.info("taskLog : {}", taskLog);
        if (taskLog == null) {
            return null;
        }
        logMapper.insert(taskLog);
        return taskLog;
    }

}
