package com.ccms.core.engine;

import java.util.HashMap;
import java.util.Map;

import com.ccms.dialect.Dialect;
import com.ccms.dialect.DialectFactory;

import dinamica.Db;
import dinamica.GenericTableManager;
import dinamica.Recordset;
import dinamica.StringUtil;

public class InjectOBTaskPool extends GenericTableManager  {
	
	//执行方法之前先判断该线程是否执行完成
	private static Boolean IS_FINISH = true;
	
	@Override
	public int service(Recordset inputParams) throws Throwable {
		
		int rc = super.service(inputParams);
		
		Db db = getDb();
				
		if(IS_FINISH == true){//注入任务池
			
			//加锁，以免上一个线程尚未执行完，下一个就执行了
			synchronized(this){
				IS_FINISH = false;
				try{
					String[] params1 =
					{
						"cust_code",
						"pk_value",
						"bz_pk_value"
					};
					Recordset rsJob = db.get(getSQL(getResource("query_job.sql"), inputParams));
					String qBaseSql = getResource("query-base.sql");
					String queryFirst = getResource("query_filter_first.sql");
					String queryChildren = getResource("query_filter_children.sql");
					String insertPool = getResource("insert.sql");
					rsJob.top();
					while(rsJob.next()){

						//业务类型
						String cs_bz_type = rsJob.getString("cs_bz_type");						
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
						
						//限制查询条数
						Integer job_quota = rsJob.getInteger("job_quota");
						Dialect dialect = DialectFactory.buildDialect(getConnection().getMetaData().getDatabaseProductName().toLowerCase());
						sql = dialect.getLimitString(sql, 0, job_quota);
						
						Recordset rs = db.get(sql);
						if(rs.getRecordCount() <= 0){
							continue;
						}
						
						//初始化清空当前的主题内的除重数据
						Map<String, String> Duplicate_For_CS_Job_Customer_Map = new HashMap<String, String>();
						Map<String, String> Duplicate_For_CS_Job_Pkvalue_Map = new HashMap<String, String>();
						Map<String, String> Duplicate_For_CS_Job_BzPkvalue_Map = new HashMap<String, String>();
						
						//新数据结果集
						Recordset rs1 = new Recordset();
						rs1.append("tuid", java.sql.Types.INTEGER);
						rs1.append("cust_code", java.sql.Types.VARCHAR);
						rs1.append("pk_value", java.sql.Types.VARCHAR);
						rs1.append("bz_pk_value", java.sql.Types.VARCHAR);
						
						//优先替换table_id,form_id
						String insert = getSQL(insertPool, rsJob);	//替换bz_type
						
						rs.top();
						while (rs.next())
						{
							//根据业务主键去重,需要判断是按客户还是按主题 除重
							String cust_code_v = rs.getString(cust_code_field).trim();
							String pk_value_v = rs.getString(pk_value_field).trim();
							String bz_pk_value_v = rs.getString(bz_pk_value_field).trim();

							//取到客户编号，为空则无效
							if(cust_code_v == null) continue;

							//抢占式(0) 非抢占式(1,3,6),只判断客户的外呼
							//使用业务类型与主键值拼解以防止存在主键相同
							//本批次除重判断(task_duplicate_flag 0-按客户,1-按CASE,2-按子CASE)
							if("0".equals(cs_task_duplicate_flag)){	//客户除重
								if(Duplicate_For_CS_Job_Customer_Map.containsKey(cust_code_v)){
									continue;
								}
							} else if("1".equals(cs_task_duplicate_flag)){	//CASE除重
								if(Duplicate_For_CS_Job_Pkvalue_Map.containsKey(cs_bz_type+"^"+pk_value_v)){
									continue;
								}
							} else if("2".equals(cs_task_duplicate_flag)){	//子CASE除重
								if(Duplicate_For_CS_Job_BzPkvalue_Map.containsKey(cs_bz_type+"^"+bz_pk_value_v)){
									continue;
								}
							}
							//除重判断完毕
							//把增的记录加到除重表里
							Duplicate_For_CS_Job_Customer_Map.put(cust_code_v, cust_code_v);
							Duplicate_For_CS_Job_Pkvalue_Map.put(cs_bz_type+"^"+pk_value_v, pk_value_v);
							Duplicate_For_CS_Job_BzPkvalue_Map.put(cs_bz_type+"^"+bz_pk_value_v,bz_pk_value_v);

							//新记录,客户抢占式判断
							rs1.addNew();
															
							if(cust_code_v != null && !"".equals(cust_code_v)){
								rs1.setValue("cust_code", cust_code_v);
							}
							
							if(pk_value_field != null && !"".equals(pk_value_field))
							{
								rs1.setValue("pk_value", rs.getString(pk_value_field));
							}
							
							if(bz_pk_value_field != null && !"".equals(bz_pk_value_field))
							{
								rs1.setValue("bz_pk_value", rs.getString(bz_pk_value_field));
							}
						}
						
						//批量新增数据
						if (rs1.getRecordCount()>0)
						{
							db.execBatch(insert, rs1, params1);
						}
					}
				}catch(Throwable e){
					throw e;
				}finally{
					IS_FINISH = true;
				}
			}
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
