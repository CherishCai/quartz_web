/*
 * Copyright (c) caihongwen.cn 2017.
 */

package cn.cherish.learn.quartz_web.quartz.job;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

/**
 * 阻塞调度
 * @author Cherish
 * @version 1.0
 * @date 2017/9/5 19:36
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class StatefulJob extends BaseJob {

}
