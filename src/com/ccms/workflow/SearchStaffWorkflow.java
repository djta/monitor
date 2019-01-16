package com.ccms.workflow;

import java.sql.Types;
import java.util.HashSet;
import java.util.Set;

import com.ccms.authority.AuthorityBean;

import dinamica.Db;
import dinamica.GenericTransaction;
import dinamica.Recordset;
import dinamica.StringUtil;
import dinamica.security.DinamicaUser;

public class SearchStaffWorkflow extends GenericTransaction
{

	public int service(Recordset inputParams) throws Throwable
	{

		int rc = super.service(inputParams);
		DinamicaUser user = (DinamicaUser) getSession().getAttribute("dinamica.security.login");
		String relation_staff = user.getName();
		
		//查询权限
		String queryAction = null;
		
		Db db = getDb();
		String stepType =  inputParams.getString("step_type");
		if("2".equals(stepType)){//如果是join节点的话需要判断其分支上的所有节点是否已完成
			String entry_id =  inputParams.getString("entry_id");
			int next_node =  inputParams.getInteger("next_node");
			int current_node = inputParams.getInteger("current_step_id");
			Set<Integer> splitStepId = new HashSet<Integer>();
			String queryStep = getLocalResource("/com/ccms/workflow/sql/query_split_step.sql");
			String queryCurrentStep = getLocalResource("/com/ccms/workflow/sql/query_all_current_steps.sql");
			searchSplitSteps(db, queryStep, splitStepId, next_node);//递归查询出分支上所有节点
			
			queryCurrentStep = StringUtil.replace(queryCurrentStep, "${entry_id}", entry_id);
			Recordset rs = db.get(queryCurrentStep);
			while(rs.next()){
				int step_id = rs.getInteger("step_id");
				if(splitStepId.contains(step_id) && current_node != step_id){
					Recordset rsOrg =  new Recordset();
					rsOrg.append("org_id", Types.VARCHAR);
					publish("query-org.sql",rsOrg);
                	//join 节点有未完成的分支节点
                	return rc;
                }
			}
			queryAction = getResource("query-action-next.sql");
		}else{
			queryAction = getResource("query-action.sql");
		}
		String query_org = getResource("query-org.sql");
		Recordset rsAction = db.get(getSQL(queryAction, inputParams));
		StringBuffer sbPost = new StringBuffer(4000);
		StringBuffer sbStaff = new StringBuffer(4000);
		if(rsAction.getRecordCount() == 0){//结单节点
			AuthorityBean auth = new AuthorityBean(getDb(), relation_staff);
			sbPost.append(auth.spliceGroupSql(null, "1", false));
			sbStaff.append(auth.spliceGroupSql(null, "2", false));
		}else{
			sbPost.append("select distinct id,name,show_order from (");
			sbStaff.append("select distinct id,name,show_order from (");
			while(rsAction.next()){
				String group_id = rsAction.getString("group_id");
				AuthorityBean auth = new AuthorityBean(getDb(), relation_staff);
				sbPost.append(auth.spliceGroupSql(group_id!=null?Integer.parseInt(group_id):null, "1", false)).append(" union ");
				sbStaff.append(auth.spliceGroupSql(group_id!=null?Integer.parseInt(group_id):null, "2", false)).append(" union ");
			}
			sbPost.delete(sbPost.lastIndexOf(" union "), sbPost.length());
			sbStaff.delete(sbStaff.lastIndexOf(" union "), sbStaff.length());
			
			sbPost.append(") nt");
			sbStaff.append(") nt order by show_order ");
		}
		
		Recordset rsPost = getDb().get(getSQL(sbPost.toString(), null));
		Recordset rsStaff = getDb().get(getSQL(sbStaff.toString(), null));
		publish("query-post.sql",rsPost);
		publish("query-staff.sql",rsStaff);

		if(rsStaff.getRecordCount()==0){
			Recordset rsOrg =  new Recordset();
			rsOrg.append("org_id", Types.VARCHAR);
			publish("query-org.sql",rsOrg);
		}else{
			//query_org = getSQL(query_org,rsPost);
			query_org = getSQL(query_org,rsStaff);
			Recordset rsOrg = getDb().get(query_org);

			publish("query-org.sql",rsOrg);
		}
		
		return rc;
		
	}

	private void searchSplitSteps(Db db, String query, Set<Integer> stepSet, Integer step_id) throws Throwable{
		String queryStep = StringUtil.replace(query, "${id}", step_id.toString());
		Recordset rs = db.get(queryStep);
		while(rs.next()){
			Integer node_id = rs.getInteger("node_id");
			stepSet.add(node_id);
			searchSplitSteps(db, query, stepSet, node_id);
		}
	}
}
