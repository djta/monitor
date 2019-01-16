package com.ccms.workflow.countersign;

import java.util.Map;

import com.ccms.workflow.util.GenericTransactionForWF;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.FunctionProvider;
import com.opensymphony.workflow.WorkflowException;
import com.opensymphony.workflow.spi.WorkflowEntry;

import dinamica.Db;
import dinamica.StringUtil;

public class CountersignNotPassFunction extends GenericTransactionForWF implements FunctionProvider {

    @SuppressWarnings("unchecked")
	public void execute(Map transientVars, Map args, PropertySet ps) throws WorkflowException {

    	try {
			WorkflowEntry entry = (WorkflowEntry) transientVars.get("entry");
			Long entry_id = entry.getId();
			
    		Db db = getDb();
    		//通过actionId查出目标step
    		String updateSql = getLocalResource("/com/ccms/workflow/sql/countersign/update-countersign_reult.sql");
    		updateSql = StringUtil.replace(updateSql, "${id}", entry_id.toString());
    		updateSql = StringUtil.replace(updateSql, "${result}", "1");
    		db.exec(updateSql);
		}catch (Throwable e) {
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
