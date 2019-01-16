package com.ccms.workflow;

import java.sql.Types;

import com.opensymphony.workflow.Workflow;
import com.opensymphony.workflow.basic.BasicWorkflow;
import com.opensymphony.workflow.loader.ActionDescriptor;
import com.opensymphony.workflow.loader.WorkflowDescriptor;

import dinamica.Db;
import dinamica.GenericTransaction;
import dinamica.Recordset;
import dinamica.security.DinamicaUser;

public class SearchActionWorkflow extends GenericTransaction
{

	public int service(Recordset inputParams) throws Throwable
	{

		int rc = super.service(inputParams);
		
		DinamicaUser user = (DinamicaUser) getSession().getAttribute("dinamica.security.login");
		Workflow wf = new BasicWorkflow(user.getName());
		
		Db db = getDb();
		
		Recordset rsAction = new Recordset();
		rsAction.append("action_id", Types.INTEGER);
		rsAction.append("action_name", Types.VARCHAR);
		rsAction.append("node_type", Types.VARCHAR);
		rsAction.append("step_type", Types.VARCHAR);
		rsAction.append("pre_class", Types.VARCHAR);
		rsAction.append("next_node", Types.INTEGER);
		
		String queryCurrentStep = getLocalResource("/com/ccms/workflow/sql/query-current_step.sql");
		Recordset rsCurrentStep = db.get(getSQL(queryCurrentStep,inputParams));
		if(rsCurrentStep.next()){
			Long entry_id = new Long(rsCurrentStep.getInteger("entry_id"));
			WorkflowDescriptor wd = wf.getWorkflowDescriptor(rsCurrentStep.getString("wfm_name"));
			int[] actions = wf.getAvailableActions(entry_id, null);
			if(actions != null && actions.length > 0){
				//只把当前文档关联的步骤处理动作查询出来
				String queryCurrentActions = getLocalResource("/com/ccms/workflow/sql/query-current_step_actions.sql");
				queryCurrentActions = getSQL(queryCurrentActions, inputParams);
				Recordset rsCurrentActions = db.get(queryCurrentActions);
				while(rsCurrentActions.next()){
					int action_id = rsCurrentActions.getInt("action_id");
					for (int i = 0; i < actions.length; i++) {
						if(action_id == actions[i]){
							ActionDescriptor actionDescriptor = wd.getAction(actions[i]);
							if(actionDescriptor == null) continue;
							rsAction.addNew();
				            rsAction.setValue("action_id", actionDescriptor.getId());
				            rsAction.setValue("action_name", actionDescriptor.getName());
				            rsAction.setValue("node_type", rsCurrentActions.getString("node_type"));
				            rsAction.setValue("step_type", rsCurrentActions.getString("step_type"));
				            rsAction.setValue("pre_class", rsCurrentActions.getString("pre_class"));
				            rsAction.setValue("next_node", rsCurrentActions.getInteger("next_node"));
				            
						}
					}
				}
			}
		}
		//按照操作名称排序
		rsAction.sort("action_name");
		
		//判断当前是否是子流程节点，并且没有产生过子流程，是的话则在界面上显示子流程处理按钮
		String queryChildWfm = getLocalResource("/com/ccms/workflow/sql/query-current_step_childwfm.sql");
		Recordset rsChildWfm = db.get(getSQL(queryChildWfm, inputParams));
		
		publish("query-action.sql",rsAction);
		publish("query-entry.sql",rsCurrentStep);
		publish("query-child_wfm.sql",rsChildWfm);
		
		return rc;
		
	}

}
