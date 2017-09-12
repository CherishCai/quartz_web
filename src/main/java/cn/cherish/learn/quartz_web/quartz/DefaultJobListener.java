/*
 * Copyright (c) caihongwen.cn 2017.
 */

package cn.cherish.learn.quartz_web.quartz;

import cn.cherish.learn.quartz_web.dal.entity.TaskLog;
import cn.cherish.learn.quartz_web.service.MailComponent;
import cn.cherish.learn.quartz_web.service.TaskLogService;
import cn.cherish.learn.quartz_web.util.NativeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 默认调度监听器
 * @author Cherish
 * @version 1.0
 * @date 2017/9/5 19:14
 */
@Slf4j
public class DefaultJobListener implements JobListener {

    private static final String LISTENER_NAME = "DefaultJobListener";
    private static final String JOB_LOG = "jobLog";
    // 线程池
    private static ExecutorService executorService = Executors.newCachedThreadPool();

    @Autowired
    private TaskLogService taskLogService;
    @Autowired
    private MailComponent mailComponent;

    @Override
    public String getName() {
        return LISTENER_NAME; //must return a name
    }

    // 任务开始前
    // Run this if job is about to be executed.
    @Override
    public void jobToBeExecuted(final JobExecutionContext context) {
        final JobDetail jobDetail = context.getJobDetail();
        final JobKey jobKey = jobDetail.getKey();
        final JobDataMap jobDataMap = jobDetail.getJobDataMap();
        log.info("定时任务开始执行：{}", jobKey);
        if (log.isDebugEnabled()) {
            log.debug("DefaultJobListener jobDataMap >>>>>>>>>>>>>>>>>");
            jobDataMap.forEach((key, val) -> log.debug("{} > {}", key, val));
        }
        // 任务日志
        TaskLog taskLog = new TaskLog();
        taskLog.setGroupName(jobKey.getGroup());
        taskLog.setTaskName(jobKey.getName());
        taskLog.setStartTime(context.getFireTime());
        taskLog.setTaskStatus(JobStateEnum.INIT_STATE.name());
        taskLog.setServerHost(NativeUtil.getHostName());
        taskLog.setServerDuid(NativeUtil.getDUID());
        log.info("任务[{}] 日志:{}", jobKey, taskLog);

        taskLog = taskLogService.insert(taskLog);// 插入任务日志
        jobDataMap.put(JOB_LOG, taskLog);// 保留给任务结束更改执行状态
    }

    // 被否决
    // No idea when will run this?
    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        final JobDetail jobDetail = context.getJobDetail();
        final JobKey jobKey = jobDetail.getKey();
        log.info("定时任务被否决：{}", jobKey);

        final JobDataMap jobDataMap = jobDetail.getJobDataMap();
        // 更新任务日志执行状态
        final TaskLog taskLog = (TaskLog) jobDataMap.get(JOB_LOG);
        if (taskLog == null) {
            log.warn("{} 该任务没有任务日志", jobKey);
            return;
        }

        taskLog.setEndTime(new Date());// 被否决时间
        if (taskLog.getTaskName().equals(JobStateEnum.INIT_STATE.name())) {
            taskLog.setTaskStatus(JobStateEnum.UNKNOW_STATE.name());
        }
        taskLog.setFireInfo("该任务被否决");

        String contactEmail = jobDataMap.getString("contactEmail");
        if (StringUtils.isNotBlank(contactEmail)) {
            String topic = String.format("调度[%s]任务被否决", jobKey.toString());
            sendEmail(contactEmail, topic, "任务被否决，请查阅相关信息");// 发送邮件
        }

        updateLog(taskLog);
    }

    // 执行结束后
    // Run this after job has been executed
    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        final JobDetail jobDetail = context.getJobDetail();
        final JobKey jobKey = jobDetail.getKey();
        log.info("定时任务执行结束：{}", jobKey);

        final JobDataMap jobDataMap = jobDetail.getJobDataMap();
        // 更新任务日志执行状态
        final TaskLog taskLog = (TaskLog) jobDataMap.get(JOB_LOG);
        if (taskLog == null) {
            log.warn("{} 该任务没有任务日志", jobKey);
            return;
        }

        taskLog.setEndTime(Calendar.getInstance().getTime());// 执行结束时间
        if (jobException != null) {// 异常了
            log.error("定时任务失败: [" + jobKey.getGroup() + "." + jobKey.getName() + "]", jobException);
            String contactEmail = jobDataMap.getString("contactEmail");
            if (StringUtils.isNotBlank(contactEmail)) {
                String topic = String.format("调度[%s]发生异常", jobKey.toString());
                sendEmail(contactEmail, topic, jobException.getMessage());// 发送邮件
            }
            taskLog.setTaskStatus(JobStateEnum.ERROR_STATE.name());
            taskLog.setFireInfo(jobException.getMessage());
            log.error("任务[{}] 日志:{}", jobKey, taskLog);
        } else {// 正常结束
            if (taskLog.getTaskStatus().equals(JobStateEnum.INIT_STATE.name())) {
                taskLog.setTaskStatus(JobStateEnum.SUCCESS_STATE.name());
            }
            long elapsed = taskLog.getEndTime().getTime() - taskLog.getStartTime().getTime();
            taskLog.setFireInfo("耗时 : " + elapsed + "ms");
        }

        updateLog(taskLog);
    }

    private void updateLog(TaskLog taskLog) {
        executorService.execute(() -> {
            try {
                taskLogService.updateLog(taskLog);
            } catch (Exception e) {
                log.error("Update TaskLog cause error. The taskLog : " + taskLog, e);
            }
        });
    }

    private void sendEmail(String contactEmail, String topic, String message) {
        executorService.execute(() -> {
            log.info("将发送邮件至: {}, topic: {}, message: {}", contactEmail, topic, message);
            mailComponent.sendSimpleMail(contactEmail, topic, message);// 发送邮件。。。
        });
    }

}
