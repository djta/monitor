package com.ccms.workflow.quartz;

import java.util.List;


import com.ccms.context.InitializerServlet;
import com.ccms.workflow.BasicWfmAction;
import com.opensymphony.workflow.spi.SimpleStep;

import dinamica.Db;
import dinamica.Recordset;
import dinamica.StringUtil;

public class AddTimeoutSchedule extends BasicWfmAction{

	@SuppressWarnings("unchecked")
	public void addTimeoutSchedule(List currentSteps, Db db) throws Throwable{
		String queryNextOutTimeAction = getLocalResource("/com/ccms/workflow/sql/query-next-out-time-action.sql");
		for(int i=0;i<currentSteps.size();i++){
			SimpleStep step = (SimpleStep)currentSteps.get(i);
			String query = StringUtil.replace(queryNextOutTimeAction, "${step_id}", String.valueOf(step.getStepId()));
			Recordset rsOutTime = db.get(query);
	    	if(rsOutTime.next()){
	    		String outtime_action_id = rsOutTime.getString("action_id");
	    		Integer quartz_timeout = rsOutTime.getInteger("quartz_timeout");
	    		OsworkflowScheduleJob.executeSchedule(OutTimeAutoExecJob.class, "OutTimeJob"+outtime_action_id, "OutTimeTrigger"+outtime_action_id, 
	    				"OutTime"+outtime_action_id, step.getEntryId(), InitializerServlet.getContext().getInitParameter("system-user"), 
	    				null, quartz_timeout.longValue(), null);
	    	}
		}
	}
}
