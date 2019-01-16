package com.ccms.workflow.countersign;

import java.util.HashMap;
import java.util.Map;

import com.ccms.workflow.util.GenericTransactionForWF;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.FunctionProvider;
import com.opensymphony.workflow.Workflow;
import com.opensymphony.workflow.WorkflowContext;
import com.opensymphony.workflow.WorkflowException;
import com.opensymphony.workflow.basic.BasicWorkflow;
import com.opensymphony.workflow.spi.WorkflowEntry;

import dinamica.Db;
import dinamica.Recordset;
import dinamica.StringUtil;

public class CountersignProductFunction extends GenericTransactionForWF implements FunctionProvider {

    @SuppressWarnings("unchecked")
	public void execute(Map transientVars, Map args, PropertySet ps) throws WorkflowException {

    	try {
			WorkflowContext context = (WorkflowContext) transientVars.get("context");
			WorkflowEntry entry = (WorkflowEntry) transientVars.get("entry");
			Long entry_id = entry.getId();
			
			Integer actionId = 0;
    		String actionIdVal = (String) args.get("action_id");
    		if (actionIdVal != null) {
                try {
                	actionId = Integer.parseInt(actionIdVal);
                } catch (Exception ex) {
                }
            }
    		if(actionId == 0) return;
    		
    		Db db = getDb();
    		
    		//通过actionId查出目标step
    		String queryStep = getLocalResource("/com/ccms/workflow/sql/countersign/query-next_step.sql");
    		queryStep = StringUtil.replace(queryStep, "${action_id}", actionId.toString());
    		Recordset rsStep = db.get(queryStep);
    		if(rsStep.next()){
    			Integer wfm_id = rsStep.getInteger("wfm_id");
    			String wfm_name = rsStep.getString("wfm_name");
    			String queryFirstAction = getLocalResource("/com/ccms/workflow/sql/query_first_action.sql");
    			queryFirstAction = StringUtil.replace(queryFirstAction, "${wfm_id}", wfm_id.toString());
    			Recordset rsAction = db.get(queryFirstAction);
    			rsAction.first();
    			Integer child_action_id = rsAction.getInteger("action_id");
    			
    			String pk_values = null;
				String entity_values = null;
				String entity_scope = null;
				String pk_value = null;
    			String queryCountersign = getLocalResource("/com/ccms/workflow/sql/countersign/query-countersign.sql");
    			queryCountersign = StringUtil.replace(queryCountersign, "${id}", entry_id.toString());
    			Recordset rsEntry = db.get(queryCountersign);
    			if(rsEntry.getRecordCount() == 0){//说明是新增主流程的第一节点就是会签节点，这时需要从Map里面取
    				pk_values = (String)transientVars.get("pk_values");
    				entity_values = (String)transientVars.get("entity_values");
    				entity_scope = (String)transientVars.get("entity_scope");
    				pk_value = (String)transientVars.get("pk_value");
    			}else{
    				rsEntry.first();
    				pk_values = rsEntry.getString("pk_values");
    				entity_values = rsEntry.getString("entity_values");
    				entity_scope = rsEntry.getString("entity_scope");
    				pk_value = rsEntry.getString("pk_value");
    			}
    			if(pk_value != null){
    				/*生成会签业务信息*/
    				if(pk_values != null && pk_values.length() > 0){
    					String insert_list = getLocalResource("/com/ccms/workflow/sql/countersign/insert-countersign_list.sql");
    					insert_list = StringUtil.replace(insert_list, "${pk_value}", pk_value);
    					insert_list = StringUtil.replace(insert_list, "${caller}", context.getCaller());
    					insert_list = getSQL(insert_list, null);
    					String[] lists = pk_values.split(",");
    					for(String val : lists){
    						if(val != null && val.length() > 0){
    							String insertSql = StringUtil.replace(insert_list, "${value}", val);
    							db.exec(insertSql);
    						}
    					}
    				}
    				
    				/*生成会签任务信息*/
    				if(entity_values != null && entity_values.length() > 0){
    					String insert = getLocalResource("/com/ccms/workflow/sql/countersign/insert-countersign_comment.sql");
    					insert = StringUtil.replace(insert, "${pk_value}", pk_value);
    					insert = StringUtil.replace(insert, "${caller}", context.getCaller());
    					insert = getSQL(insert, null);
    					if("0".equalsIgnoreCase(entity_scope) || "3".equalsIgnoreCase(entity_scope)){//部门或司局
    						insert = StringUtil.replace(insert, "${field}", "org_id");
    					}else if("1".equalsIgnoreCase(entity_scope)){//岗位
    						insert = StringUtil.replace(insert, "${field}", "post_id");
    					}else if("2".equalsIgnoreCase(entity_scope)){//人
    						insert = StringUtil.replace(insert, "${field}", "userlogin");
    					}
    					String update = getLocalResource("/com/ccms/workflow/sql/countersign/update-wfentry.sql");
    					update = getSQL(update, rsStep);
    					update = StringUtil.replace(update, "${parent_id}", entry_id.toString());
    					update = StringUtil.replace(update, "${caller}", context.getCaller());
    					String[] values = entity_values.split(",");
    					Workflow wf = new BasicWorkflow(context.getCaller());
    					//给该流程增加处理人限定
    					Map map = new HashMap();
    					for(String val : values){
    						if(val != null && val.length() > 0){
    							//批量初始化子流程
    							if("0".equalsIgnoreCase(entity_scope) || "3".equalsIgnoreCase(entity_scope)){//部门或司局
    								map.put("owner", val+"_org");
    	    					}else if("1".equalsIgnoreCase(entity_scope)){//岗位
    	    						map.put("owner", val+"_post");
    	    					}else if("2".equalsIgnoreCase(entity_scope)){//人
    	    						map.put("owner", val);
    	    					}
    							Long childEntryId = wf.initialize(wfm_name, child_action_id, map);
    							String insertSql = StringUtil.replace(insert, "${value}", val);
    							db.exec(insertSql);
    							String updateSql = StringUtil.replace(update, "${id}", childEntryId.toString());
    							db.exec(updateSql);
    						}
    					}
    				}
    			}
    		}
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
