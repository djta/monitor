package com.ccms.workflow;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import com.opensymphony.workflow.Workflow;
import com.opensymphony.workflow.basic.BasicWorkflow;

import dinamica.Db;
import dinamica.GenericTableManager;
import dinamica.Recordset;
import dinamica.StringUtil;
import dinamica.security.DinamicaUser;

public class CreateWorkflow_ extends GenericTableManager
{

	@SuppressWarnings("unchecked")
	public int service(Recordset inputParams,Db db) throws Throwable
	{

		int rc = 0;
		String userlogin = inputParams.getString("userlogin");
		String pk_value_ = inputParams.getString("pk_value");
		String table_id = inputParams.getString("table_id");
		//DinamicaUser user = (DinamicaUser) getSession().getAttribute("dinamica.security.login");
		
		//事务控制
		Workflow wf = new BasicWorkflow(userlogin);
		
		String wfm_id = inputParams.getString("__wfm_id__");
		
		if(wfm_id == null || wfm_id.length()==0){//直接退出
			return rc;
		}
		
		String queryWfm = getLocalResource("/com/ccms/workflow/sql/query-wfm.sql");
		String updateWfentry = getLocalResource("/com/ccms/workflow/sql/update-wfentry.sql");
		String queryFirstAction = getLocalResource("/com/ccms/workflow/sql/query_first_action.sql");
		queryWfm = StringUtil.replace(queryWfm, "${wfm_id}", wfm_id);
		queryFirstAction = StringUtil.replace(queryFirstAction, "${wfm_id}", wfm_id);
		Recordset rsWfm = db.get(queryWfm);
		if(rsWfm.getRecordCount() == 0){
			throw new Throwable("流程信息不存在！");
		}
		Recordset rsAction = db.get(queryFirstAction);
		if(rsAction.getRecordCount() == 0){
			throw new Throwable("流程创建失败（没有可执行的动作）！");
		}
	    try{
	    	updateWfentry = getSQL(updateWfentry,inputParams);
	    	updateWfentry = StringUtil.replace(updateWfentry, "${wfm_id}", wfm_id);
	    	
	    	//初始化流程信息
	    	rsWfm.first();
	    	rsAction.first();
			String wfm_name = rsWfm.getString("wfm_name");
			Integer action_id = rsAction.getInteger("action_id");
			
			Map map = new HashMap();
			map.put("owner",userlogin);
			//判断拟稿后首个节点是否是会签节点，如果是的话需要把会签相关的几个字段取出来放到Map里面
			String queryNextStep = getLocalResource("/com/ccms/workflow/sql/query_next_step_type.sql");
			queryNextStep = StringUtil.replace(queryNextStep, "${action_id}", action_id.toString());
			Recordset rsNextStep = db.get(queryNextStep);
			rsNextStep.first();
			String step_type = rsNextStep.getString("step_type");
			if("3".equalsIgnoreCase(step_type)){
				String pk_value = inputParams.getString("bpk_field_value");
				String entity_values = getRequest().getParameter("entity_values");
				String entity_scope = getRequest().getParameter("entity_scope");
				String pk_values = getRequest().getParameter("pk_values");
				map.put("pk_value", pk_value);
				map.put("entity_values", entity_values);
				map.put("entity_scope", entity_scope);
				map.put("pk_values", pk_values);
			}
			
	    	long id = wf.initialize(wfm_name, action_id, map);
	    	
	    	//将流程实例和当前步骤回写
			inputParams.append("wfentry_id", Types.VARCHAR);
			inputParams.append("current_step_id", Types.VARCHAR);
			inputParams.addNew();
	    	inputParams.setValue("wfentry_id", id);
	    	inputParams.setValue("current_step_id", rsAction.getInteger("next_node"));
	    	
	    	//将业务信息更新到实例表
	    	updateWfentry = StringUtil.replace(updateWfentry, "${id}", id+"");
	    	updateWfentry = StringUtil.replace(updateWfentry, "${wfm_id}", wfm_id);
	    	updateWfentry = StringUtil.replace(updateWfentry, "${fld:table_id}", table_id);
	    	updateWfentry = StringUtil.replace(updateWfentry, "${fld:bpk_field_value}", pk_value_);
	    	updateWfentry = StringUtil.replace(updateWfentry, "${fld:__p_pk_value__}", null);
	    	updateWfentry = StringUtil.replace(updateWfentry, "${fld:__parent_wfentry_id__}", null);
	    	updateWfentry = StringUtil.replace(updateWfentry, "${fld:__parent_node_id__}", null);
	    	db.exec(updateWfentry);

	    }catch(Throwable e){
	    	e.printStackTrace();
	    	throw new Throwable("流程创建失败！");
	    }
		return rc;
		
	}

}
