package com.ccms.workflow;

import java.util.HashMap;
import java.util.Map;

import com.opensymphony.workflow.Workflow;
import com.opensymphony.workflow.basic.BasicWorkflow;

import dinamica.GenericTransaction;
import dinamica.Recordset;
import dinamica.security.DinamicaUser;

public class ReturnDoActionWorkflow extends GenericTransaction
{

	@SuppressWarnings("unchecked")
	public int service(Recordset inputParams) throws Throwable
	{

		int rc = super.service(inputParams);
		
		DinamicaUser user = (DinamicaUser) getSession().getAttribute("dinamica.security.login");
		
		Workflow wf = new BasicWorkflow(user.getName());
		
		Recordset rsLast = getDb().get(getSQL(getResource("query-last-step.sql"), inputParams));
		
		if(rsLast.next()){
			String owner = rsLast.getString("owner");
			long wfentry_id = inputParams.getInteger("__wfentry_id__").longValue();
			int action_id = rsLast.getInt("action_id");
			Map map = new HashMap();
			map.put("owner", owner);
		    try{
		    	//执行步骤
		    	wf.doAction(wfentry_id, action_id, map);

		    }catch(Throwable e){
		    	e.printStackTrace();
		    	throw new Throwable("退回上一处理人执行失败！");
		    }
		}else{
			throw new Throwable("当前操作无法执行，请检查流程状态！");
		}
		return rc;
		
	}
}
