package com.ccms.workflow.quartz;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.util.TextUtils;
import com.opensymphony.workflow.FunctionProvider;
import com.opensymphony.workflow.WorkflowContext;
import com.opensymphony.workflow.spi.WorkflowEntry;

public class ScheduleJob implements FunctionProvider {

    private static final Log log = LogFactory.getLog(ScheduleJob.class);

    @SuppressWarnings("unchecked")
	public void execute(Map transientVars, Map args, PropertySet ps) {
        try {
            WorkflowEntry entry = (WorkflowEntry) transientVars.get("entry");
            WorkflowContext context = (WorkflowContext) transientVars.get("context");

            log.info("Starting to schdule job for WF #" + entry.getId());

            int triggerId = TextUtils.parseInt((String) args.get("triggerId"));
            String jobName = (String) args.get("jobName");
            String triggerName = (String) args.get("triggerName");
            String groupName = (String) args.get("groupName");
            String username = context.getCaller();
            String cronExpression = (String) args.get("cronExpression");
            String quartzDelay = (String) args.get("quartzDelay");
            Long delay = null;
            if(quartzDelay != null && quartzDelay.length() > 0){
            	delay = Long.parseLong(quartzDelay);
            }
            
            OsworkflowScheduleJob.executeSchedule(LocalWorkflowJob.class, jobName, triggerName, groupName, entry.getId(), username, triggerId, delay, cronExpression);
            
            log.info("Job scheduled");
        } catch (Throwable e) {
        	log.error("Error scheduling job", e);
		}
    }
}
