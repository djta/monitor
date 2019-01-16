package com.ccms.workflow;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.ccms.authority.AuthorityBean;
import com.opensymphony.workflow.Workflow;
import com.opensymphony.workflow.basic.BasicWorkflow;

import dinamica.Db;
import dinamica.GenericTableManager;
import dinamica.GenericTransaction;
import dinamica.Recordset;
import dinamica.StringUtil;
import dinamica.security.DinamicaUser;

public class UpdateDoActionWorkflow extends GenericTableManager
{

	@SuppressWarnings("unchecked")
	public int service(Recordset inputParams) throws Throwable
	{

		int rc = super.service(inputParams);
		
		DinamicaUser user = (DinamicaUser) getSession().getAttribute("dinamica.security.login");
		
		Workflow wf = new BasicWorkflow(user.getName());
		
		Integer wfentry_id = inputParams.getInteger("__wfentry_id__");
		Integer action_id = inputParams.getInteger("__action_id__");
		String assign_user = inputParams.getString("__assign_user__");
		
		//判断当前传入的action_id 是否有效
		int[] actions = wf.getAvailableActions(wfentry_id.longValue(),null);
		if(actions == null || actions.length <= 0) return rc;
		boolean flag = false;
		for(int i=0;i<actions.length;i++){
			if(action_id == actions[i]){
				flag = true;
				break;
			}
		}
		if(flag == false){
			throw new Throwable("当前操作无法执行，请检查流程状态！");
		}
		
		Map map = new HashMap();
		if(assign_user != null && assign_user.length() > 0){
			map.put("owner", assign_user);
		}
	    try{
	    	Db db = getDb();
	    	//先判断当前人是否为被授权处理该操作，是的话需要记录一下日志
	    	String queryStepOwner = getSQL(getResource("query-step_owner.sql"), inputParams);
	    	Recordset rsOwner = db.get(queryStepOwner);
	    	if(rsOwner.next()){
	    		db.exec(getSQL(getResource("insert-grant_log.sql"),rsOwner));
	    	}
	    	
	    	//执行步骤
	    	wf.doAction(wfentry_id.longValue(), action_id, map);
	    	
	    	//判断下一节点如果是分支则需要特殊处理currentstep owner
	    	Recordset rsNext = getRecordset("query-next-step.sql");
	    	rsNext.top();
	    	if(rsNext.next()){
	    		String next_step_type = rsNext.getString("next_step_type");
	    		if("1".equals(next_step_type)){
	    			String node_id = rsNext.getString("next_node");
	    			String queryNN = getSQL(getResource("query-next-next.sql"), inputParams);
	    			queryNN = StringUtil.replace(queryNN, "${node_id}", node_id);
	    			Recordset rsNN = db.get(queryNN);
	    			Map<String, StringBuffer> staffMap = new HashMap<String, StringBuffer>();
	    			if(assign_user == null || assign_user.length() == 0){
	    				assign_user = user.getName();
	    			}
	    			AuthorityBean auth = new AuthorityBean(getDb(), assign_user);
	    			while(rsNN.next()){
	    				String node = rsNN.getString("next_node");
	    				String authority = rsNN.getString("authority_id");
	    				StringBuffer sbStaff = null;
	    				if(staffMap.containsKey(node)){
	    					sbStaff = staffMap.get(node);
	    				}else{
	    					sbStaff = new StringBuffer(4000);
	    					sbStaff.append("select distinct id from (");
	    				}
	    				sbStaff.append(auth.spliceGroupSql(authority!=null?Integer.parseInt(authority):null, "2", false)).append(" union ");
	    				staffMap.put(node, sbStaff);
	    			}
	    			String updateStep = getSQL(getResource("update-currentstep.sql"), inputParams);
	    			Iterator<String> itAuth = staffMap.keySet().iterator();
	    			while(itAuth.hasNext()){
	    				String key = itAuth.next();
	    				StringBuffer sb = staffMap.get(key);
	    				sb.delete(sb.lastIndexOf(" union "), sb.length());
	    				sb.append(") nt ");
	    				Recordset rsStaff = getDb().get(getSQL(sb.toString(), null));
	    				sb.delete(0, sb.length());
	    				while(rsStaff.next()){
	    					String id = rsStaff.getString("id");
	    					if((sb.length()+id.length()+2) > 512){
	    						break;
	    					}
	    					sb.append("#").append(rsStaff.getString("id")).append("#");
	    				}
	    				if(sb.length() > 0){
	    					String update = StringUtil.replace(updateStep, "${step_id}", key);
	    					update = StringUtil.replace(update, "${owner}", sb.toString());
	    					db.exec(update);
	    				}
	    			}
	    		}
	    	}
	    	
	    	//执行后处理的一些操作，包括CS流程、提醒、超时自动提交任务、会签判断
	    	ClassLoader loaderAfter = Thread.currentThread().getContextClassLoader();
			GenericTransaction tAfter = (GenericTransaction) loaderAfter.loadClass("com.ccms.workflow.afterservice.AfterServiceWorkflow").newInstance();
						
			tAfter.init(this.getContext(), this.getRequest(), this.getResponse());
			tAfter.setConfig(this.getConfig());
			tAfter.setConnection(this.getConnection());
			tAfter.service(inputParams);

	    }catch(Throwable e){
	    	e.printStackTrace();
	    	throw new Throwable("流程执行失败！");
	    }
		
		return rc;
		
	}
}
