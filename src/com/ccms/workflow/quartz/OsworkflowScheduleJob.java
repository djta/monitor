package com.ccms.workflow.quartz;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Date;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

public class OsworkflowScheduleJob{

    @SuppressWarnings("unchecked")
	public static void executeSchedule(Class jobClass, String jobName, String triggerName, String groupName, Long entry_id, String username, Integer triggerId, Long delay, String cronExpression) throws Throwable{
    	jobName = jobName + ":" + entry_id;
        triggerName = triggerName + ":" + entry_id;
        groupName = groupName + ":" + entry_id;
        SchedulerFactory factory = new StdSchedulerFactory();
        Scheduler s = factory.getScheduler();
        JobDataMap dataMap = new JobDataMap();
        dataMap.put("triggerId", triggerId);
        dataMap.put("entryId", entry_id);
        dataMap.put("username", username);
        
        JobDetail jobDetail = newJob(jobClass).withIdentity(jobName, groupName).usingJobData(dataMap).build();
        //判断是否已经存在任务，存在就返回
        if(s.checkExists(jobDetail.getKey())) return;
        
        Trigger trigger;

        if (cronExpression == null || "".equals(cronExpression)) {
            long now = System.currentTimeMillis();

            Date startDate = null;

            if (s != null && delay != null) {
                startDate = new Date(now + delay*60*1000);	//分钟转成毫秒
            }

            if (startDate == null) {
                startDate = new Date(now);
            }

            trigger = newTrigger().withIdentity(triggerName, groupName).startAt(startDate).withSchedule(simpleSchedule().withRepeatCount(0).withIntervalInSeconds(1)).build();
        } else {
            trigger = newTrigger().withIdentity(triggerName, groupName).withSchedule(cronSchedule(cronExpression)).build();
        }

        s.scheduleJob(jobDetail, trigger);

    }
}
