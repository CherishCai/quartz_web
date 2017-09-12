/*
 * Copyright (c) caihongwen.cn 2017.
 */

package cn.cherish.learn.quartz_web.quartz.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.context.ApplicationContext;

/**
 * 默认调度(非阻塞)
 * @author Cherish
 * @version 1.0
 * @date 2017/9/5 19:36
 */
@Slf4j
public class BaseJob implements Job {

    public void execute(JobExecutionContext context) throws JobExecutionException {
        long start = System.currentTimeMillis();
        JobDetail jobDetail = context.getJobDetail();
        JobKey jobKey = jobDetail.getKey();
        try {
            actualExecute(context);// 真正的执行逻辑
        } catch (Exception e) {
            throw new JobExecutionException(e);
        }
        long time = (System.currentTimeMillis() - start);
        log.info("定时任务[{}] 用时：{}ms", jobKey, time);
    }

    /**
     * 真正的执行逻辑
     * @param context JobExecutionContext
     * @throws Exception 异常
     */
    protected void actualExecute(JobExecutionContext context) throws Exception {
        log.debug("[BaseJob] actualExecute");
        ApplicationContext applicationContext = (ApplicationContext) context.getScheduler().getContext().get("applicationContext");
        log.debug("[BaseJob] applicationContext : {} , you can do anything get from it", applicationContext);
    }

}