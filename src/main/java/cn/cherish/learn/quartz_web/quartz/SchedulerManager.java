/*
 * Copyright (c) caihongwen.cn 2017.
 */

package cn.cherish.learn.quartz_web.quartz;

import cn.cherish.learn.quartz_web.dal.entity.TaskConfig;
import cn.cherish.learn.quartz_web.quartz.job.ReadDBTaskJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author Cherish
 * @version 1.0
 * @date 2017/9/5 18:55
 */
@Slf4j
public class SchedulerManager implements InitializingBean {

    private final Scheduler scheduler;

    @Autowired
    public SchedulerManager(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 初始化一个查询数据库里的任务配置
        String jobName = "task_for_all";
        String jobGroup = "group_for_all";
        try {
            JobDetail detail = scheduler.getJobDetail(JobKey.jobKey(jobName, jobGroup));
            JobDetail jobDetail = JobBuilder.newJob(ReadDBTaskJob.class)
                    .withIdentity(jobName, jobGroup)
                    .withDescription("读取数据库任务")
                    .storeDurably(true)
                    .build();

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withSchedule(CronScheduleBuilder.cronSchedule("33 0/5 * * * ?"))
                    .withIdentity(jobName, jobGroup)
                    .withDescription("读取数据库任务")
                    .forJob(jobDetail)
                    .build();
            if (detail == null) {
                scheduler.scheduleJob(jobDetail, trigger);
            } else {
                scheduler.addJob(jobDetail, true);
                scheduler.rescheduleJob(TriggerKey.triggerKey(jobName, jobGroup), trigger);
            }
        } catch (SchedulerException e) {
            log.error("初始化读取数据库任务失败 ", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 新增或更新job
     * @param taskConfig taskConfig 计划任务信息
     * @return boolean 成功返回 true, 失败则抛异常 RuntimeException
     * @throws RuntimeException 失败则抛异常
     */
    public boolean addOrUpdateTask(TaskConfig taskConfig) {
        log.debug("taskConfig : {}", taskConfig);
        String jobGroup = taskConfig.getGroupName();
        String jobName = taskConfig.getTaskName();
        if (StringUtils.isBlank(jobGroup) || StringUtils.isBlank(jobName)) {
            throw new IllegalArgumentException("jobGroup 和 jobName 都不允许为空");
        }

        try {
            JobDetail jobDetail = scheduler.getJobDetail(new JobKey(jobName, jobGroup));
            if (jobDetail == null) {// 未存在过的任务
                String cronExpression = taskConfig.getTaskCron();// cronExpression表达式
                String jobDescription = taskConfig.getTaskDesc();// 任务描述

                JobDataMap jobDataMap = new JobDataMap();// 附带数据
                jobDataMap.put("contactName", taskConfig.getContactName());
                jobDataMap.put("contactEmail", taskConfig.getContactEmail());

                JobDetail newJobDetail = JobBuilder.newJob(cn.cherish.learn.quartz_web.quartz.job.StatefulJob.class)
                        .withIdentity(jobName, jobGroup)
                        .withDescription(jobDescription)
                        .usingJobData(jobDataMap)
                        .storeDurably(true)
                        .build();

                Trigger newTrigger = TriggerBuilder.newTrigger()
                        .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                        .withIdentity(jobName, jobGroup)
                        .withDescription(jobDescription)
                        .usingJobData(jobDataMap)
                        .forJob(newJobDetail)
                        .build();
                scheduler.scheduleJob(newJobDetail, newTrigger);
            } else {            // 对于已经存在的任务
                TriggerKey triggerKey = new TriggerKey(jobName, jobGroup);
                Trigger trigger = scheduler.getTrigger(triggerKey);
                // TODO 判定jobDetail,trigger是否已经有所修改

                String cronExpression = taskConfig.getTaskCron();// cronExpression表达式
                String jobDescription = taskConfig.getTaskDesc();// 任务描述

                JobDataMap jobDataMap = new JobDataMap();// 附带数据
                jobDataMap.put("contactName", taskConfig.getContactName());
                jobDataMap.put("contactEmail", taskConfig.getContactEmail());

                jobDetail = jobDetail.getJobBuilder()
                        .withDescription(jobDescription)
                        .usingJobData(jobDataMap)
                        .storeDurably(true)
                        .build();

                trigger = TriggerBuilder.newTrigger()
                        .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                        .withIdentity(jobName, jobGroup)
                        .withDescription(jobDescription)
                        .usingJobData(jobDataMap)
                        .forJob(jobDetail)
                        .build();

                scheduler.addJob(jobDetail, true);
                scheduler.rescheduleJob(triggerKey, trigger);
            }
        } catch (SchedulerException e) {
            log.error("addOrUpdateTask ", e);
            throw new RuntimeException(e);
        }
        return true;
    }

    /**
     * 获取 scheduler 下的所有JobDetail信息
     * @return LinkedList<taskConfig>
     */
    public List<TaskConfig> getAllJobDetail() {
        List<TaskConfig> list = new LinkedList<>();
        try {
            GroupMatcher<JobKey> matcher = GroupMatcher.jobGroupContains("");
            Set<JobKey> jobKeys = scheduler.getJobKeys(matcher);
            for (JobKey jobKey : jobKeys) {
                JobDetail jobDetail = scheduler.getJobDetail(jobKey);
                List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
                for (Trigger trigger : triggers) {
                    TaskConfig taskConfig = new TaskConfig();
                    taskConfig.setTaskName(jobKey.getName());
                    taskConfig.setGroupName(jobKey.getGroup());
                    // 任务描述
                    taskConfig.setTaskDesc(jobDetail.getDescription());
                    // status
                    Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
                    taskConfig.setTaskStatus(triggerState.name());
                    // taskCron
                    if (trigger instanceof CronTrigger) {
                        CronTrigger cronTrigger = (CronTrigger) trigger;
                        String cronExpression = cronTrigger.getCronExpression();
                        taskConfig.setTaskCron(cronExpression);
                    }
                    // 上次时间，下次时间
                    taskConfig.setPreviousFireTime(trigger.getPreviousFireTime());
                    taskConfig.setNextFireTime(trigger.getNextFireTime());
                    // jobDataMap
                    JobDataMap jobDataMap = trigger.getJobDataMap();
                    taskConfig.setContactName(jobDataMap.getString("contactName"));
                    taskConfig.setContactEmail(jobDataMap.getString("contactEmail"));

                    list.add(taskConfig);
                    log.debug("taskConfig {}", taskConfig);
                }
            }
        } catch (Exception e) {
            log.error("Try to load All JobDetail cause error : ", e);
        }
        return list;
    }

    public JobDetail getJobDetailByTrigger(Trigger trigger) {
        try {
            return this.scheduler.getJobDetail(trigger.getJobKey());
        } catch (Exception e) {
            log.error("getJobDetailByTriggerName ", e);
        }
        return null;
    }

    /**
     * 暂停所有触发器
     */
    public void pauseAllTrigger() {
        try {
            scheduler.standby();
        } catch (SchedulerException e) {
            log.error("pauseAllTrigger ", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 启动所有触发器
     */
    public void startAllTrigger() {
        try {
            if (scheduler.isInStandbyMode()) {
                scheduler.start();
            }
        } catch (SchedulerException e) {
            log.error("startAllTrigger ", e);
            throw new RuntimeException(e);
        }
    }

    // 暂停任务
    public void stopJob(TaskConfig taskConfig) {
        try {
            JobKey jobKey = JobKey.jobKey(taskConfig.getTaskName(), taskConfig.getGroupName());
            scheduler.pauseJob(jobKey);
        } catch (Exception e) {
            log.error("Try to stop Job cause error : ", e);
            throw new RuntimeException(e);
        }
    }

    // 启动任务
    public void resumeJob(TaskConfig taskConfig) {
        try {
            JobKey jobKey = JobKey.jobKey(taskConfig.getTaskName(), taskConfig.getGroupName());
            scheduler.resumeJob(jobKey);
        } catch (Exception e) {
            log.error("Try to resume Job cause error : ", e);
            throw new RuntimeException(e);
        }
    }

    // 执行任务
    public void runJob(TaskConfig taskConfig) {
        try {
            JobKey jobKey = JobKey.jobKey(taskConfig.getTaskName(), taskConfig.getGroupName());
            scheduler.triggerJob(jobKey);
        } catch (Exception e) {
            log.error("Try to resume Job cause error : ", e);
            throw new RuntimeException(e);
        }
    }

    // 删除任务
    public void delJob(TaskConfig taskConfig) {
        try {
            JobKey jobKey = JobKey.jobKey(taskConfig.getTaskName(), taskConfig.getGroupName());
            TriggerKey triggerKey = TriggerKey.triggerKey(taskConfig.getTaskName(), taskConfig.getGroupName());
            scheduler.pauseTrigger(triggerKey);// 停止触发器
            scheduler.unscheduleJob(triggerKey);// 移除触发器
            scheduler.deleteJob(jobKey);// 删除任务
        } catch (Exception e) {
            log.error("Try to resume Job cause error : ", e);
            throw new RuntimeException(e);
        }
    }


}
