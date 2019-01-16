package com.ccms.workflow.countersign;

import java.util.HashMap;
import java.util.Map;

import com.opensymphony.workflow.Workflow;
import com.opensymphony.workflow.basic.BasicWorkflow;

import dinamica.Db;
import dinamica.GenericTableManager;
import dinamica.Recordset;
import dinamica.StringUtil;
import dinamica.security.DinamicaUser;

/**
 * @author zhangchuan 会签的审核任务创建的后处理类
 * 
 */
public class CountersignProductAfterClass extends GenericTableManager {

	@SuppressWarnings("unchecked")
	public int service(Recordset inputParams) throws Throwable {
		int rc = 0;
		DinamicaUser user = (DinamicaUser) getSession().getAttribute("dinamica.security.login");
		Db db = getDb();
		//从审核任务表获取主键
		Recordset rsForm = (Recordset) getRequest().getAttribute("query-form.sql");
		rsForm.first();
		String pk_value = rsForm.getString("bpk_field_value");
		String queryFirstAction = getLocalResource("/com/ccms/workflow/sql/countersign/query_first_action_child.sql");
		queryFirstAction = getSQL(queryFirstAction, inputParams);
		Recordset rsAction = db.get(queryFirstAction);
		if (rsAction.next()) {
			String wfm_name = rsAction.getString("wfm_name");
			Integer child_action_id = rsAction.getInteger("action_id");

			String __parent_wfentry_id__ = inputParams.getString("__parent_wfentry_id__");
			String __parent_node_id__ = inputParams.getString("__parent_node_id__");

			String pk_values = getRequest().getParameter("pk_values");
			String entity_values = getRequest().getParameter("entity_values");
			String entity_scope = getRequest().getParameter("entity_scope");

			/* 生成会签业务信息 */
			if (pk_values != null && pk_values.length() > 0) {
				String insert_list = getLocalResource("/com/ccms/workflow/sql/countersign/insert-countersign_list.sql");
				insert_list = StringUtil.replace(insert_list, "${pk_value}", pk_value);
				insert_list = getSQL(insert_list, inputParams);
				String[] lists = pk_values.split(",");
				for (String val : lists) {
					if (val != null && val.length() > 0) {
						String insertSql = StringUtil.replace(insert_list, "${value}", val);
						db.exec(insertSql);
					}
				}
			}

			/* 生成会签任务信息 */
			if (entity_values != null && entity_values.length() > 0) {
				String insert = getLocalResource("/com/ccms/workflow/sql/countersign/insert-countersign_comment.sql");
				insert = StringUtil.replace(insert, "${pk_value}", pk_value);
				insert = getSQL(insert, inputParams);
				
				//根据父级会签节点的岗位类型配置来进一步筛选人员
				String queryCountersignPost = getLocalResource("/com/ccms/workflow/sql/countersign/query_countersign_post.sql");
				String queryPost = getLocalResource("/com/ccms/workflow/sql/countersign/query_org_post.sql");
				queryCountersignPost = StringUtil.replace(queryCountersignPost, "${node_id}", __parent_node_id__);
				Recordset rsCP = db.get(queryCountersignPost);
				rsCP.first();
				String cp = rsCP.getString("countersign_post");
				boolean postFlag = false;
				
				if ("0".equalsIgnoreCase(entity_scope) || "3".equalsIgnoreCase(entity_scope)) {// 部门或司局
					insert = StringUtil.replace(insert, "${field}", "org_id");
					if(cp != null && cp.length() > 0){
						postFlag = true;
						queryPost = StringUtil.replace(queryPost, "${countersign_post}", cp);
					}
				} else if ("1".equalsIgnoreCase(entity_scope)) {// 岗位
					insert = StringUtil.replace(insert, "${field}", "post_id");
				} else if ("2".equalsIgnoreCase(entity_scope)) {// 人
					insert = StringUtil.replace(insert, "${field}", "userlogin");
				}
				String update = getLocalResource("/com/ccms/workflow/sql/countersign/update-wfentry-child.sql");
				update = getSQL(update, rsAction);
				update = StringUtil.replace(update, "${parent_id}", __parent_wfentry_id__);
				update = StringUtil.replace(update, "${parent_node_id}", __parent_node_id__);
				String[] values = entity_values.split(",");
				Workflow wf = new BasicWorkflow(user.getName());
				// 给该流程增加处理人限定
				Map map = new HashMap();
				for (String val : values) {
					if (val != null && val.length() > 0) {
						// 批量初始化子流程
						if ("0".equalsIgnoreCase(entity_scope) || "3".equalsIgnoreCase(entity_scope)) {// 部门或司局
							map.put("owner", val + "_org");
							if(postFlag == true){//看看能否找到对应部门下面的岗位，如果有则取一条，没有则放到部门上面
								String query = StringUtil.replace(queryPost, "${org_id}", val);
								Recordset rsPost = db.get(query);
								if(rsPost.next()){
									String org_post_id = rsPost.getString("org_post_id");
									map.put("owner", org_post_id + "_post");
								}
							}
						} else if ("1".equalsIgnoreCase(entity_scope)) {// 岗位
							map.put("owner", val + "_post");
						} else if ("2".equalsIgnoreCase(entity_scope)) {// 人
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
		return rc;
	}
}
