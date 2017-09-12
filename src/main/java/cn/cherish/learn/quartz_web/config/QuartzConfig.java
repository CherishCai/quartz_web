/*
 * Copyright (c) caihongwen.cn 2017.
 */

package cn.cherish.learn.quartz_web.config;

import cn.cherish.learn.quartz_web.quartz.DefaultJobListener;
import cn.cherish.learn.quartz_web.quartz.SchedulerManager;
import org.quartz.Scheduler;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Cherish
 * @version 1.0
 * @date 2017/9/6 0:16
 */
@Configuration
public class QuartzConfig {

    /**
     * @see cn.cherish.learn.quartz_web.quartz.SchedulerManager
     */
    @Bean
    public SchedulerManager schedulerManager(Scheduler scheduler) {
        return new SchedulerManager(scheduler);
    }

    @Bean
    public Scheduler scheduler(SchedulerFactoryBean schedulerFactoryBean) {
        return schedulerFactoryBean.getScheduler();
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(
            @Qualifier("quartzDataSource") DataSource quartzDataSource,
            JobFactory jobFactory) throws IOException {

        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        // 用于quartz集群,QuartzScheduler 启动时更新己存在的Job
        schedulerFactoryBean.setOverwriteExistingJobs(true);
        // 延时启动，应用启动30秒后
        schedulerFactoryBean.setStartupDelay(10);
        schedulerFactoryBean.setAutoStartup(true);
        // quartz配置
        schedulerFactoryBean.setQuartzProperties(quartzProperties());
        // 全局监听器
        schedulerFactoryBean.setGlobalJobListeners(defaultJobListener());
        // 数据源
        schedulerFactoryBean.setDataSource(quartzDataSource);
        schedulerFactoryBean.setApplicationContextSchedulerContextKey("applicationContext");
        // 如果你的job中需要注入Spring容器中的对象
        schedulerFactoryBean.setJobFactory(jobFactory);

        return schedulerFactoryBean;
    }

    @Bean
    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("quartz.properties"));
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }

    /**
     * @see cn.cherish.learn.quartz_web.quartz.DefaultJobListener
     */
    @Bean
    public DefaultJobListener defaultJobListener(){
        return new DefaultJobListener();
    }

    @Bean
    public JobFactory jobFactory(ApplicationContext applicationContext) {
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    /**
     * Spring自动装配Job
     */
    public static class AutowiringSpringBeanJobFactory
            extends SpringBeanJobFactory implements ApplicationContextAware {

        private transient AutowireCapableBeanFactory beanFactory;

        @Override
        public void setApplicationContext(final ApplicationContext context) {
            beanFactory = context.getAutowireCapableBeanFactory();
        }

        @Override
        protected Object createJobInstance(final TriggerFiredBundle bundle) throws Exception {
            final Object job = super.createJobInstance(bundle);
            beanFactory.autowireBean(job);
            return job;
        }
    }

}
