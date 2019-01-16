package com.ccms.workflow.quartz;

import com.opensymphony.workflow.Workflow;
import com.opensymphony.workflow.WorkflowException;
import com.opensymphony.workflow.basic.BasicWorkflow;
import com.opensymphony.workflow.config.Configuration;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class LocalWorkflowJob implements Job {

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap data = jobExecutionContext.getMergedJobDataMap();
        long id = data.getLong("entryId");
        int triggerId = data.getInt("triggerId");
        String username = data.getString("username");
        Workflow wf = new BasicWorkflow(username);
        wf.setConfiguration((Configuration) data.get("configuration"));

        try {
            wf.executeTriggerFunction(id, triggerId);
        } catch (WorkflowException e) {
            throw new JobExecutionException("Error Executing local job", (e.getRootCause() != null) ? (Exception) e.getRootCause() : e, true);
        }
    }
}
