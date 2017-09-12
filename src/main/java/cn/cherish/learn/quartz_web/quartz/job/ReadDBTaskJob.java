/*
 * Copyright (c) caihongwen.cn 2017.
 */

package cn.cherish.learn.quartz_web.quartz.job;

import cn.cherish.learn.quartz_web.service.TaskConfigService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Cherish
 * @version 1.0
 * @date 2017/9/6 13:57
 */
@Slf4j
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class ReadDBTaskJob extends BaseJob {

    @Autowired
    private TaskConfigService taskConfigService;

    /**
     * 真正的执行逻辑
     * @param context JobExecutionContext
     * @throws Exception 异常
     */
    @Override
    protected void actualExecute(JobExecutionContext context) throws Exception {
        taskConfigService.scheduleAllTask();
    }
}
