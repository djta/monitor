package com.ccms.workflow.quartz;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ccms.workflow.util.GenericTransactionForWF;
import com.opensymphony.workflow.Workflow;
import com.opensymphony.workflow.basic.BasicWorkflow;

import dinamica.Db;
import dinamica.Recordset;
import dinamica.StringUtil;

/**
 * @author zc
 * 超时自动执行 action 不考虑结单节点
 *
 */
public class OutTimeAutoExecJob extends GenericTransactionForWF implements Job {

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap data = jobExecutionContext.getMergedJobDataMap();
        Long id = data.getLong("entryId");
        String username = data.getString("username");
        Workflow wf = new BasicWorkflow(username);
        
        try{
        	Db db = getDb();
        	String queryOutTimeAction = getLocalResource("/com/ccms/workflow/sql/query-out-time-action.sql");
        	queryOutTimeAction = StringUtil.replace(queryOutTimeAction, "${entry_id}", id.toString());
        	Recordset rs = db.get(queryOutTimeAction);
        	if(rs.next()){
        		Integer action_id = rs.getInteger("action_id"); 
        		wf.doAction(id, action_id, null);
        	}
        }catch(Throwable e){
        	e.printStackTrace();
        }finally{
        	try {
				super.close();
			} catch (Throwable e) {
				e.printStackTrace();
			}
        }
    }
}
