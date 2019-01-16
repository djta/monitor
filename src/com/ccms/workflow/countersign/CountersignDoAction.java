package com.ccms.workflow.countersign;

import java.util.HashMap;
import java.util.Map;


import com.ccms.Constants;
import com.ccms.context.InitializerServlet;
import com.ccms.workflow.BasicWfmAction;
import com.opensymphony.workflow.Workflow;
import com.opensymphony.workflow.basic.BasicWorkflow;
import com.opensymphony.workflow.spi.WorkflowEntry;

import dinamica.Db;
import dinamica.Recordset;
import dinamica.StringUtil;

public class CountersignDoAction extends BasicWfmAction {

	/**
	 * @param parent_id 父实例ID
	 * @param parent_node_id 父流程当前节点
	 * @param step_type 3：会签；4：子流程；
	 * @param db
	 * @throws Throwable
	 */
	@SuppressWarnings("unchecked")
	public void doCountersign(Integer parent_id, Integer parent_node_id, String step_type, Db db) throws Throwable{
		//如果父节点类型为子流程节点则不需校验其他子流程状态
		boolean gotoFlag = false;
		if("4".equalsIgnoreCase(step_type)){
			gotoFlag = true;
		}else if("3".equalsIgnoreCase(step_type)){
			String queryChildrenEntry = getLocalResource("/com/ccms/workflow/sql/query-wfentry_byparent.sql");
			queryChildrenEntry = StringUtil.replace(queryChildrenEntry, "${parent_id}", parent_id.toString());
			queryChildrenEntry = StringUtil.replace(queryChildrenEntry, "${parent_node_id}", parent_node_id.toString());
			Recordset rsChildren = db.get(queryChildrenEntry);
			if(rsChildren.getRecordCount() == 0){//说明子流程都已经处理完，可以触发主流程流转（默认会签节点的动作都由系统用户来执行）
				gotoFlag = true;
			}
		}
		
		if(gotoFlag == true){
			//取系统用户
			String systemUser = InitializerServlet.getContext().getInitParameter(Constants.SYSTEM_USER);
			Workflow wfParent = new BasicWorkflow(systemUser);
			int[] parentActions = wfParent.getAvailableActions(parent_id.longValue(),null);
			if(parentActions.length > 0){
				String queryNextActions = getLocalResource("/com/ccms/workflow/sql/query-next-action-bynode.sql");
				queryNextActions = StringUtil.replace(queryNextActions, "${node_id}", parent_node_id.toString());
				Recordset rsActions = db.get(queryNextActions);
				Integer parentDoAction = null;
				while(rsActions.next()){
					int id = rsActions.getInt("action_id");
					for(int v : parentActions){
						if(id == v){//存在符合条件的动作
							parentDoAction = v;
							break;
						}
					}
					if(parentDoAction != null){
						break;
					}
				}
				if(parentDoAction != null){//执行父流程动作
					//数据所属人返回给会签发起人
					String queryStepOwner = getLocalResource("/com/ccms/workflow/sql/countersign/query_currentstep_owner.sql");
					queryStepOwner = StringUtil.replace(queryStepOwner, "${entry_id}", parent_id.toString());
					queryStepOwner = StringUtil.replace(queryStepOwner, "${step_id}", parent_node_id.toString());
					Recordset rsStepOwner = db.get(queryStepOwner);
					Map map = new HashMap();
					if(rsStepOwner.next()){
						map.put("owner", rsStepOwner.getString("owner"));
					}
					wfParent.doAction(parent_id.longValue(), parentDoAction, map);
					
					//判断该父流程是否是子流程，如果是子流程则判断是否主流程的所有子流程都处理完成，然后触发主流程动作
			    	String queryEntry = getLocalResource("/com/ccms/workflow/sql/query-wfentry_byid.sql");
			    	queryEntry = StringUtil.replace(queryEntry, "${id}", parent_id.toString());
		    		Recordset rsEntry = db.get(queryEntry);
		    		rsEntry.first();
		    		String parent_parent_id = rsEntry.getString("parent_id");
		    		if(parent_parent_id != null){//存在主流程
		    			//判断当前子流程是否结束，如果结束则判断其他子流程是否全结束，以此来触发主流程继续流转
		    			int state = wfParent.getEntryState(parent_id.longValue());
		    			if(state == WorkflowEntry.COMPLETED){
		    				String parent_parent_node_id = rsEntry.getString("parent_node_id");
		    				if(parent_node_id != null){
		    					String parent_step_type = rsEntry.getString("step_type");
		    					CountersignDoAction countersign = new CountersignDoAction();
		        				countersign.doCountersign(Integer.parseInt(parent_parent_id), Integer.parseInt(parent_parent_node_id), parent_step_type, db);
		    				}
		    			}
		    		}
				}
			}
		}
	}
}
