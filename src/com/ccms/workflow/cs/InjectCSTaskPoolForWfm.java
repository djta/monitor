package com.ccms.workflow.cs;

import com.ccms.core.engine.SearchClauseUtil;

import dinamica.Db;
import dinamica.GenericTableManager;
import dinamica.Recordset;
import dinamica.StringUtil;


public class InjectCSTaskPoolForWfm extends GenericTableManager  {
	
	@Override
	public int service(Recordset inputParams) throws Throwable {
		
		int rc = 0;
		
		Db db = getDb();
		
		String query_job = getSQL(getLocalResource("/com/ccms/workflow/sql/cs/query_job.sql"), inputParams);
		Recordset rsJob = db.get(query_job);//此处会校验任务是否有效（开始时间、结束时间、是否启用）
		if(rsJob.next()){
			String insert = getSQL(getLocalResource("/com/ccms/workflow/sql/cs/insert.sql"), rsJob);
			//替换pk_value
			String qBaseSql = getSQL(getLocalResource("/com/ccms/workflow/sql/cs/query-base.sql"), inputParams);
			String queryFirst = getSQL(getLocalResource("/com/ccms/workflow/sql/cs/query_filter_first.sql"), rsJob);
			String queryChildren = getSQL(getLocalResource("/com/ccms/workflow/sql/cs/query_filter_children.sql"), rsJob);
			
			//新数据结果集
			Recordset rs1 = new Recordset();
			rs1.append("cust_code", java.sql.Types.VARCHAR);
			rs1.append("pk_value", java.sql.Types.VARCHAR);
			rs1.append("bz_pk_value", java.sql.Types.VARCHAR);
	
			//业务类型
			String cs_task_duplicate_scope = rsJob.getString("task_duplicate_scope");						
			String cs_task_duplicate_flag = rsJob.getString("task_duplicate_flag");						
			String cust_code_field = rsJob.getString("cust_code");
			String pk_value_field = rsJob.getString("pk_value");
			String bz_pk_value_field = rsJob.getString("bz_pk_value");
			
			String qBase = qBaseSql;
			String search_sql = rsJob.getString("search_sql");
			if(!(search_sql==null || "".equals(search_sql))){
				qBase = search_sql;
			}
			
			String table_code = rsJob.getString("table_code");

			qBase = StringUtil.replace(qBase, "${table}", table_code);
			qBase = StringUtil.replace(qBase, "${field}", cust_code_field+","+pk_value_field+","+bz_pk_value_field);//仅取关键字段
			
			String filterString = SearchClauseUtil.getFilters(db, getSQL(queryFirst, rsJob), getSQL(queryChildren, rsJob), table_code, cust_code_field);
			qBase = StringUtil.replace(qBase, "${filter}", filterString);
			
			//根据除重范围替换查询列
			if("0".equals(cs_task_duplicate_scope)){//活动
				qBase = StringUtil.replace(qBase, "${pool_duplicate_scope_field}", "campaign_id");
				qBase = StringUtil.replace(qBase, "${duplicate_scope_field}", "${fld:campaign_id}");
			}else if("1".equals(cs_task_duplicate_scope)){//主题
				qBase = StringUtil.replace(qBase, "${pool_duplicate_scope_field}", "job_id");
				qBase = StringUtil.replace(qBase, "${duplicate_scope_field}", "${fld:job_id}");
			}
			
			//根据不同的除重对象替换查询列
			if("0".equals(cs_task_duplicate_flag)){//客户
				qBase = StringUtil.replace(qBase, "${pool_unique_field}", "cust_code");
				qBase = StringUtil.replace(qBase, "${unique_field}", cust_code_field);
			}else if("1".equals(cs_task_duplicate_flag)){//case
				qBase = StringUtil.replace(qBase, "${pool_unique_field}", "pk_value");
				qBase = StringUtil.replace(qBase, "${unique_field}", pk_value_field);
			}else if("2".equals(cs_task_duplicate_flag)){//子case
				qBase = StringUtil.replace(qBase, "${pool_unique_field}", "bz_pk_value");
				qBase = StringUtil.replace(qBase, "${unique_field}", bz_pk_value_field);
			}
			
			String sql = getSQL(qBase, rsJob);
			Recordset rs = db.get(sql);
			if(rs.getRecordCount() <= 0){
				return rc;
			}
			
			rs.first();
				
			//取到客户编号，为空则无效
			String cust_code_v = rs.getString(cust_code_field);
			if(cust_code_v == null) return rc;
											
			rs1.addNew();
			
			String pk_value_v = rs.getString(pk_value_field);
			String bz_pk_value_v = rs.getString(bz_pk_value_field);
			
			if(cust_code_v != null && !"".equals(cust_code_v)){
				rs1.setValue("cust_code", cust_code_v);
			}
			
			if(pk_value_v != null && !"".equals(pk_value_v))
			{
				rs1.setValue("pk_value", rs.getString(pk_value_v));
			}
			
			if(bz_pk_value_v != null && !"".equals(bz_pk_value_v))
			{
				rs1.setValue("bz_pk_value", rs.getString(bz_pk_value_v));
			}
			db.exec(getSQL(insert, rs1));
		}
		return rc;
	}
	
	protected Integer getTuid(String sql) throws Throwable {
		Integer tuid = null;
		Recordset rs = getDb().get(sql);
		if (rs.next()) {
			tuid = rs.getInteger("tuid");
		}
		return tuid;
	}
}
