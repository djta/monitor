package com.ccms.workflow.quartz;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

/**
 * @author zhangchuan (Mail:zhangchuanhz@gmail.com)
 * @date 2012-1-21 下午10:07:44
 * @version 1.0.0
 *
 */
public class ScheduleStartListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		SchedulerFactory scheduleFactory = new StdSchedulerFactory();
		try {
			Scheduler scheduler = scheduleFactory.getScheduler();
			scheduler.shutdown();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		SchedulerFactory scheduleFactory = new StdSchedulerFactory();
		try {
			Scheduler scheduler = scheduleFactory.getScheduler();
			scheduler.start();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

}
