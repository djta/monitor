package com.ccms.imp;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dinamica.Db;
import dinamica.GenericTableManager;
import dinamica.GenericTransaction;
import dinamica.Recordset;
import dinamica.StringUtil;

public class ImportExcel extends GenericTableManager{

	private String insert = null;
	private String update = null;
	private String querySeq = null;
	private String queryExists = null;
	private String queryLimit = null;
	private String queryFirstFilter = null;
	private String queryChildrenFilter = null;
	private String queryAfterSQL = null;
	
	private Map<String, String> titleMap = null;
	
	@SuppressWarnings("unchecked")
	public int service(Recordset inputParams) throws Throwable {
		int rc = super.service(inputParams);
		//记录时间
		long begin_time = System.currentTimeMillis();
		Date beginDate = new Date();
		
		Recordset rsData = (Recordset)getRequest().getSession().getAttribute("rsData");
		titleMap = (Map<String, String>)getRequest().getSession().getAttribute("titleMap");
		
		//获取导入信息的前置类和后置类
		Recordset rsImport = getRecordset("query-import.sql");
		rsImport.first();
		String before_class_name = (String) keyIsNull(rsImport, "before_class_name");
		//rsImport.isNull("before_class_name") ? null : rsImport.getString("before_class_name");
		String pre_class_name = (String) keyIsNull(rsImport, "pre_class_name");//rsImport.isNull("pre_class_name") ? null : rsImport.getString("pre_class_name");
		String post_class_name = (String) keyIsNull(rsImport, "post_class_name");//rsImport.isNull("post_class_name") ? null : rsImport.getString("post_class_name");
		String after_sql = (String) keyIsNull(rsImport, "after_sql");//rsImport.isNull("after_sql") ? null : rsImport.getString("after_sql");
		
		Db db = getDb();
		//执行处理前操作类（一般用于清空表等操作）
		if(before_class_name != null && before_class_name.length() > 0){
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			GenericTransaction t = (GenericTransaction) loader.loadClass(before_class_name).newInstance();
						
			t.init(this.getContext(), this.getRequest(), this.getResponse());
			t.setConfig(this.getConfig());
			t.setConnection(this.getConnection());
			t.service(rsData);
		}
		
		
		//导入表结构拼成树形结构
		Recordset rsTable = getRecordset("query-table.sql");
		String queryFields = getResource("query-fields.sql");
		String queryChildTables = getResource("query-child-table.sql");
		String queryRules = getResource("query-rules.sql");
		//按顺序执行表操作
		List<TableBean> tableList = new ArrayList<TableBean>();
		rsTable.top();
		while(rsTable.next()){
			TableBean tableBean = TableBean.transformTable(rsTable);
			setChildTable(tableBean, queryChildTables, queryFields, queryRules, tableList);
		}
		
		insert = getResource("insert.sql");
		update = getResource("update.sql");
		querySeq = getResource("query_bpk_seq.sql");
		queryExists = getResource("query_exists.sql");
		queryLimit = getResource("query_limit.sql");
		queryFirstFilter = getResource("query_filter_first.sql");
		queryChildrenFilter = getResource("query_filter_children.sql");
		queryAfterSQL = (String) keyIsNull(rsImport, "after_sql");
		
		//存储第一次的执行语句，避免每次都拼接
		Map<Integer, String> insertMap = new HashMap<Integer, String>();
		Map<Integer, String> updateMap = new HashMap<Integer, String>();
		Map<Integer, String> seqMap = new HashMap<Integer, String>();
		Map<Integer, String> ruleMap = new HashMap<Integer, String>();
		Map<Integer, String> limitMap = new HashMap<Integer, String>();
		for(TableBean bean : tableList){
			setSQL(bean, insertMap, updateMap, seqMap, ruleMap, limitMap, inputParams);
		}
		//记录总数
		Map<Integer, Integer> insertCountMap = new HashMap<Integer, Integer>();
		Map<Integer, Integer> updateCountMap = new HashMap<Integer, Integer>();
		Map<Integer, Integer> errorCountMap = new HashMap<Integer, Integer>();
		
		Integer count = rsData.getRecordCount();//总记录数
		Integer insertCount = 0;
		Integer updateCount = 0;
		Integer errorCount = 0;
		
		//记录每次新增或者修改的业务表主键信息
		String[] dataCols = {"table_id","table_code","op_type","pk_value","subject_id"};
		String insertDataLog = getSQL(getResource("insert-data-log.sql"),inputParams);
		Recordset rsDataLog = new Recordset();
		rsDataLog.append("table_id", java.sql.Types.INTEGER);
		rsDataLog.append("table_code", java.sql.Types.VARCHAR);
		rsDataLog.append("op_type", java.sql.Types.VARCHAR);
		rsDataLog.append("pk_value", java.sql.Types.VARCHAR);
		rsDataLog.append("subject_id", java.sql.Types.INTEGER);
		
		//逐条处理数据
		rsData.top();
		while(rsData.next()){
			
			//逐条数据作为一个事务处理
			db.beginTrans();

			//执行前置类
			if(pre_class_name != null && pre_class_name.length() > 0){
				ClassLoader loader = Thread.currentThread().getContextClassLoader();
				GenericTransaction t = (GenericTransaction) loader.loadClass(pre_class_name).newInstance();
							
				t.init(this.getContext(), this.getRequest(), this.getResponse());
				t.setConfig(this.getConfig());
				t.setConnection(this.getConnection());
				t.service(rsData);
			}
			//获取单条数据校验结果
			String error = rsData.getString("error");
			
			//逐表处理新增或者修改
			for(TableBean bean : tableList){
				
				//先判断当前table的存储规则（成功数据、失败数据、全部），默认成功数据
				if(error != null && error.length() > 0 && (bean.getData_operator_type() == null || "0".equals(bean.getData_operator_type()))){//成功数据
					continue;
				}
				
				if((error == null || error.length() == 0) && "1".equals(bean.getData_operator_type())){//错误数据
					continue;
				}
				
				//首先判断数据是否符合入库条件
				boolean limit = isLimit(rsData, bean, limitMap);
				if(limit){//不允许入库
					if(errorCountMap.containsKey(bean.getTab_id())){
						errorCountMap.put(bean.getTab_id(), errorCountMap.get(bean.getTab_id())+1);
					}else{
						errorCountMap.put(bean.getTab_id(), 1);
					}
					errorCount ++;
					continue;
				}
				
				String tuid = isExists(rsData, bean, ruleMap);
				boolean is_new = false;
				if(tuid == null){
					if("1".equals(bean.getIf_new_flag())){//可以新增数据
						tuid = getTuid(bean, seqMap);
						is_new = true;
					}
				}
				if(tuid == null){
					if(errorCountMap.containsKey(bean.getTab_id())){
						errorCountMap.put(bean.getTab_id(), errorCountMap.get(bean.getTab_id())+1);
					}else{
						errorCountMap.put(bean.getTab_id(), 1);
					}
					errorCount ++;
					continue;
				}
				
				//首先把主键设置到数据池中
				rsData.setValue(bean.getBpk_field_alias(), tuid);
				if(bean.getBpk_field_type() != null && "integer".equalsIgnoreCase(bean.getBpk_field_type())){//判断主键是否为整形
					rsData.setValue(bean.getBpk_field_alias()+"_code", Integer.parseInt(tuid));
				}else{
					rsData.setValue(bean.getBpk_field_alias()+"_code", tuid);
				}
				
				if(is_new){//新增数据
					if(insertMap.containsKey(bean.getTab_id())){
						String insertSQL = insertMap.get(bean.getTab_id());
						insertSQL = getSQL(insertSQL,rsData);
						db.exec(insertSQL);
						
						rsDataLog.addNew();
						rsDataLog.setValue("table_id", bean.getTable_id());
						rsDataLog.setValue("table_code", bean.getTable_code());
						rsDataLog.setValue("op_type", "0");
						rsDataLog.setValue("pk_value", tuid);
						rsDataLog.setValue("subject_id", bean.getSubject_id());
						
						if(insertCountMap.containsKey(bean.getTab_id())){
							insertCountMap.put(bean.getTab_id(), insertCountMap.get(bean.getTab_id())+1);
						}else{
							insertCountMap.put(bean.getTab_id(), 1);
						}
						insertCount ++;
					}else{
						if(errorCountMap.containsKey(bean.getTab_id())){
							errorCountMap.put(bean.getTab_id(), errorCountMap.get(bean.getTab_id())+1);
						}else{
							errorCountMap.put(bean.getTab_id(), 1);
						}
						errorCount ++;
					}
				}else{//修改数据
					if(updateMap.containsKey(bean.getTab_id())){
						String updateSQL = updateMap.get(bean.getTab_id());
						db.exec(getSQL(updateSQL,rsData));
						
						rsDataLog.addNew();
						rsDataLog.setValue("table_id", bean.getTable_id());
						rsDataLog.setValue("table_code", bean.getTable_code());
						rsDataLog.setValue("op_type", "1");
						rsDataLog.setValue("pk_value", tuid);
						rsDataLog.setValue("subject_id", bean.getSubject_id());
						
						if(updateCountMap.containsKey(bean.getTab_id())){
							updateCountMap.put(bean.getTab_id(), updateCountMap.get(bean.getTab_id())+1);
						}else{
							updateCountMap.put(bean.getTab_id(), 1);
						}
						updateCount ++;
					}else{
						if(errorCountMap.containsKey(bean.getTab_id())){
							errorCountMap.put(bean.getTab_id(), errorCountMap.get(bean.getTab_id())+1);
						}else{
							errorCountMap.put(bean.getTab_id(), 1);
						}
						errorCount ++;
					}
				}
			}
			
			//执行后置类
			if(post_class_name != null && post_class_name.length() > 0){
				ClassLoader loader = Thread.currentThread().getContextClassLoader();
				GenericTransaction t = (GenericTransaction) loader.loadClass(post_class_name).newInstance();
							
				t.init(this.getContext(), this.getRequest(), this.getResponse());
				t.setConfig(this.getConfig());
				t.setConnection(this.getConnection());
				t.service(rsData);
			}

			//执行后处理SQL
			if(queryAfterSQL!=null && queryAfterSQL.length()>0){
				if(queryAfterSQL.indexOf(";")>0){	//有多条SQL
					String each_after_sql[] = queryAfterSQL.split(";");
					for(int i=0;i<each_after_sql.length;i++){
						each_after_sql[i] = getSQL(each_after_sql[i],rsData);
						db.exec(each_after_sql[i]);
					}
				}else{
					String afterSQL = getSQL(queryAfterSQL,rsData);
					db.exec(afterSQL);
				}
			}
			
			db.commit();
		}
		
		//记录日志
		String insertLog = getSQL(getResource("insert-log.sql"),inputParams);
		String insertTableLog = getSQL(getResource("insert-table-log.sql"),inputParams);
		
		Recordset rsLog = new Recordset();
		rsLog.append("load_date", java.sql.Types.TIMESTAMP);
		rsLog.append("load_time", java.sql.Types.VARCHAR);
		rsLog.append("total_record", java.sql.Types.INTEGER);
		rsLog.append("insert_record", java.sql.Types.INTEGER);
		rsLog.append("update_record", java.sql.Types.INTEGER);
		rsLog.append("error_count", java.sql.Types.INTEGER);
		
		Recordset rsTableLog = new Recordset();
		rsTableLog.append("tab_id", java.sql.Types.INTEGER);
		rsTableLog.append("total_record", java.sql.Types.INTEGER);
		rsTableLog.append("insert_record", java.sql.Types.INTEGER);
		rsTableLog.append("update_record", java.sql.Types.INTEGER);
		rsTableLog.append("error_count", java.sql.Types.INTEGER);
		
		rsLog.addNew();
		rsLog.setValue("load_date", beginDate);
		rsLog.setValue("load_time", String.valueOf((System.currentTimeMillis()-begin_time)/1000));
		rsLog.setValue("total_record", count);
		rsLog.setValue("insert_record", insertCount);
		rsLog.setValue("update_record", updateCount);
		rsLog.setValue("error_count", errorCount);
		
		//同一个事务处理日志
		db.beginTrans();
		db.exec(getSQL(insertLog, rsLog));
		for(TableBean bean : tableList){
			
			String sql = StringUtil.replace(insertTableLog, "${tab_id}", bean.getTab_id().toString());
			Integer tableCount = 0;
			if(insertCountMap.containsKey(bean.getTab_id())){
				tableCount += insertCountMap.get(bean.getTab_id());
				sql = StringUtil.replace(sql, "${insert_record}", insertCountMap.get(bean.getTab_id()).toString());
			}else{
				sql = StringUtil.replace(sql, "${insert_record}", "0");
			}
			if(updateCountMap.containsKey(bean.getTab_id())){
				tableCount += updateCountMap.get(bean.getTab_id());
				sql = StringUtil.replace(sql, "${update_record}", updateCountMap.get(bean.getTab_id()).toString());
			}else{
				sql = StringUtil.replace(sql, "${update_record}", "0");
			}
			if(errorCountMap.containsKey(bean.getTab_id())){
				tableCount += errorCountMap.get(bean.getTab_id());
				sql = StringUtil.replace(sql, "${error_count}", errorCountMap.get(bean.getTab_id()).toString());
			}else{
				sql = StringUtil.replace(sql, "${error_count}", "0");
			}
			sql = StringUtil.replace(sql, "${total_record}", tableCount.toString());
			db.exec(sql);
		}
		db.commit();
		
		//批处理导入记录日志
		db.execBatch(insertDataLog, rsDataLog, dataCols);
		
		//清理session数据
		
		getRequest().getSession().removeAttribute("rsTitle");
		getRequest().getSession().removeAttribute("rsData");
		getRequest().getSession().removeAttribute("titleMap");
		
		return rc;
	}
	
	/**
	 * 获取序列
	 * @param bean
	 * @param seqMap
	 * @return
	 * @throws Throwable
	 */
	protected String getTuid(TableBean bean, Map<Integer, String> seqMap) throws Throwable {
		String sql = seqMap.get(bean.getTab_id());
		Recordset rsSeq = getDb().get(getSQL(sql,null));
		rsSeq.first();
		return rsSeq.getString("tuid");
	}
	
	/**
	 * 除重获取tuid
	 * @param rsData
	 * @param bean
	 * @param ruleMap
	 * @return
	 * @throws Throwable
	 */
	protected String isExists(Recordset rsData, TableBean bean, Map<Integer, String> ruleMap) throws Throwable {
		String tuid = null;
		Iterator<RuleBean> ruleIt = bean.getRuleSet().iterator();
		while(ruleIt.hasNext()){
			RuleBean ruleBean = ruleIt.next();
			if(!"0".equalsIgnoreCase(ruleBean.getFilter_type())){
				continue;
			}
			Integer rule_id = ruleBean.getRule_id();
			if(ruleMap.containsKey(rule_id)){
				String sql = ruleMap.get(rule_id);
				Recordset rs = getDb().get(getSQL(sql,rsData));
				while(rs.next()){
					tuid = rs.getString("tuid");
					break;
				}
			}
			if(tuid != null) break;
		}
		return tuid;
	}
	
	/**
	 * 数据是否被允许入库
	 * @param rsData
	 * @param bean
	 * @param limitMap
	 * @return false=允许 true=不允许
	 * @throws Throwable
	 */
	protected boolean isLimit(Recordset rsData, TableBean bean, Map<Integer, String> limitMap) throws Throwable {
		boolean limit = false;
		Iterator<RuleBean> ruleIt = bean.getRuleSet().iterator();
		while(ruleIt.hasNext()){
			RuleBean ruleBean = ruleIt.next();
			if(!"2".equalsIgnoreCase(ruleBean.getFilter_type())){
				continue;
			}
			Integer rule_id = ruleBean.getRule_id();
			if(limitMap.containsKey(rule_id)){
				String sql = limitMap.get(rule_id);
				Recordset rs = getDb().get(getSQL(sql,rsData));
				if(rs.getRecordCount() == 0){
					return true;
				}
			}
		}
		return limit;
	}
	
	/**
	 * 缓存导入配置信息
	 * @param bean
	 * @param sqlTable
	 * @param sqlField
	 * @param sqlRule
	 * @param parent_id
	 * @param tableList
	 * @throws Throwable
	 */
	protected void setChildTable(TableBean bean, String sqlTable, String sqlField, String sqlRule, List<TableBean> tableList) throws Throwable {
		String oldSqlTable = sqlTable;
		String oldSqlField = sqlField;
		String oldSqlRule = sqlRule;
		
		sqlField = StringUtil.replace(sqlField, "${tab_id}", bean.getTab_id().toString());
		Recordset rsField = getDb().get(sqlField);
		while(rsField.next()){
			FieldBean fieldBean = FieldBean.transformField(rsField);
			bean.addFieldBean(fieldBean);
		}
		
		sqlRule = StringUtil.replace(sqlRule, "${tab_id}", bean.getTab_id().toString());
		Recordset rsRule = getDb().get(sqlRule);
		while(rsRule.next()){
			bean.addRule(RuleBean.transformRule(rsRule));
		}
		
		tableList.add(bean);
		
		sqlTable = StringUtil.replace(sqlTable, "${parent_id}", bean.getTab_id().toString());
		Recordset rsTable = getDb().get(sqlTable);
		while(rsTable.next()){
			TableBean tableBean = TableBean.transformTable(rsTable);
			setChildTable(tableBean, oldSqlTable, oldSqlField, oldSqlRule, tableList);
			bean.addChildTable(tableBean);
		}
	}
	
	/**
	 * 缓存执行语句
	 * @param bean
	 * @param insertMap
	 * @param updateMap
	 * @param seqMap
	 * @param ruleMap
	 * @throws Throwable
	 */
	protected void setSQL(TableBean bean, Map<Integer, String> insertMap, Map<Integer, String> updateMap, Map<Integer, String> seqMap, Map<Integer, String> ruleMap, Map<Integer, String> limitMap, Recordset inputs) throws Throwable {
		String schema = bean.getSchema_name();
		//主键序列
		String seq = StringUtil.replace(querySeq, "${bpk_field_prefix}", bean.getBpk_field_prefix()==null?"":bean.getBpk_field_prefix());
		seq = StringUtil.replace(seq, "${bpk_field_seq}", bean.getBpk_field_seq());
		seqMap.put(bean.getTab_id(), seq);
		
		//拼接除重条件语句
		String sqlRule = StringUtil.replace(queryExists, "${table_schema}", schema!=null?schema+".":"");
		sqlRule = StringUtil.replace(sqlRule, "${table}", bean.getTable_code());
		sqlRule = StringUtil.replace(sqlRule, "${bpk_field}", bean.getBpk_field());
		Iterator<RuleBean> ruleIt = bean.getRuleSet().iterator();
		while(ruleIt.hasNext()){
			RuleBean ruleBean = ruleIt.next();
			Integer rule_id = ruleBean.getRule_id();
			
			String first = StringUtil.replace(queryFirstFilter, "${rule_id}", rule_id.toString());
			String filterSql = SearchClauseImp.getFirstFilter(getDb(), first, queryChildrenFilter, titleMap, ruleBean.getFilter_type());
			if(filterSql.length() > 0){
				if("0".equalsIgnoreCase(ruleBean.getFilter_type())){
					ruleMap.put(rule_id, StringUtil.replace(sqlRule, "${filter}", filterSql));
				}else if("2".equalsIgnoreCase(ruleBean.getFilter_type())){
					limitMap.put(rule_id, StringUtil.replace(queryLimit, "${filter}", filterSql));
				}
			}
		}
		
		//拼接新增和修改语句
		StringBuffer insertFieldPhrase = new StringBuffer();
		StringBuffer insertValuePhrase = new StringBuffer();
		StringBuffer updatePhrase = new StringBuffer();
		
		Iterator<FieldBean> fieldIt = bean.getFieldSet().iterator();
		while(fieldIt.hasNext()){
			FieldBean field = fieldIt.next();
			
			//虚拟字段不参与处理
			if("1".equalsIgnoreCase(field.getIs_virtual_type())){
				continue;
			}
			
			String field_code = field.getField_code();
			//0-不更新,1-覆盖更新,2-原值为空更新,3-追加更新(必须是字符串类型)
			String update_mode = field.getUpdate_mode();
			boolean is_exists = titleMap.containsKey(field.getCol_name());
			if (is_exists) {
				String valueCode = titleMap.get(field.getCol_name());
				//根据配置判断取值是否取code值
				if("1".equals(field.getIs_save_code())){
					valueCode = valueCode+"_code";
				}
				
				insertFieldPhrase.append(",").append(field_code);
				if(field.getDefault_value() != null && field.getDefault_value().length() > 0){
					insertValuePhrase.append(",(case when ${fld:").append(valueCode).append("} is null then ")
					.append(field.getDefault_value()).append(" else ${fld:").append(valueCode).append("} end)");
				}else{
					insertValuePhrase.append(",${fld:").append(valueCode).append("}");
				}
				
				if("1".equalsIgnoreCase(update_mode)){
					updatePhrase.append(",").append(field_code).append(" = ${fld:").append(valueCode).append("}");
				}else if("2".equalsIgnoreCase(update_mode)){
					if("varchar".equalsIgnoreCase(field.getField_type())){
						updatePhrase.append(",").append(field_code).append(" = (case when (").append(field_code).append(" is null or ").append(field_code).append("='') then ")
						.append("${fld:").append(valueCode).append("} else ").append(field_code).append(" end) ");
					}else{
						updatePhrase.append(",").append(field_code).append(" = (case when ").append(field_code).append(" is null then ")
						.append("${fld:").append(valueCode).append("} else ").append(field_code).append(" end) ");
					}
				}else if("3".equalsIgnoreCase(update_mode) && "varchar".equalsIgnoreCase(field.getField_type())){
					updatePhrase.append(",").append(field_code).append(" = concat(").append(field_code).append(",${fld:").append(valueCode).append("})");
				}
			}else{
				if(field.getDefault_value() != null && field.getDefault_value().length() > 0){
					
					String defaultValue = field.getDefault_value();
					
					if("1".equalsIgnoreCase(field.getIs_formula())){//是否是公式
						//替换标记
						Set<String> mark = getMarkers(defaultValue);
						for(String fld : mark){
							if(titleMap.containsKey(fld)){
								defaultValue = StringUtil.replace(defaultValue, "${"+fld+"}", "${fld:"+titleMap.get(fld)+"_code}");
							}else{
								Iterator<FieldBean> fieldBeanIt = bean.getFieldSet().iterator();
								while(fieldBeanIt.hasNext()){
									FieldBean fb = fieldBeanIt.next();
									if(fb.getField_code().equalsIgnoreCase(fld)){
										String d = fb.getDefault_value();
										defaultValue = StringUtil.replace(defaultValue, "${"+fld+"}", d==null?"":d);
									}
								}
							}
						}
					}
					
					defaultValue = getSQL(defaultValue,inputs);
					
					insertFieldPhrase.append(",").append(field_code);
					insertValuePhrase.append(",").append(defaultValue);
					
					if(!"0".equalsIgnoreCase(update_mode)){
						updatePhrase.append(",").append(field_code).append(" = ").append(defaultValue);
					}
				}
			}
		}
		if(insertFieldPhrase.length() > 0){
			String sql = StringUtil.replace(insert, "${field}", insertFieldPhrase.toString());
			sql = StringUtil.replace(sql, "${field_value}", insertValuePhrase.toString());
			sql = StringUtil.replace(sql, "${table_schema}", schema!=null?schema+".":"");
			sql = StringUtil.replace(sql, "${table}", bean.getTable_code());
			sql = StringUtil.replace(sql, "${bpk_field}", bean.getBpk_field());
			sql = StringUtil.replace(sql, "${pk_value}", "${fld:"+bean.getBpk_field_alias()+"_code}");
			insertMap.put(bean.getTab_id(), sql);
		}
		if(updatePhrase.length() > 0){
			updatePhrase = updatePhrase.delete(0, 1);
			String sql = StringUtil.replace(update, "${field_mark}", updatePhrase.toString());
			sql = StringUtil.replace(sql, "${table_schema}", schema!=null?schema+".":"");
			sql = StringUtil.replace(sql, "${table}", bean.getTable_code());
			sql = StringUtil.replace(sql, "${bpk_field}", bean.getBpk_field());
			sql = StringUtil.replace(sql, "${pk_value}", "${fld:"+bean.getBpk_field_alias()+"_code}");
			updateMap.put(bean.getTab_id(), sql);
		}
		
		if(queryAfterSQL!=null && queryAfterSQL.length()>0){//后处理SQL
			//替换标记
			Set<String> mark = getMarkers(queryAfterSQL);
			for(String fld : mark){
				if(titleMap.containsKey(fld)){
					queryAfterSQL = StringUtil.replace(queryAfterSQL, "${"+fld+"}", "${fld:"+titleMap.get(fld)+"_code}");
				}
			}
		}
	}
	
	public Set<String> getMarkers(String _template) throws Throwable
	{	
		
		int pos = 0;
		Set<String> array = new HashSet<String>();
		
		while ( pos >= 0 )
		{
			int pos1 = 0;
			int pos2 = 0;
			int newPos = 0;

			pos1 = _template.indexOf("${", pos);
			if (pos1>=0)
			{
				newPos = pos1+2;
				pos2 = _template.indexOf("}", newPos);
				
				if (pos2>0)
				{
					String fld = _template.substring(newPos, pos2);
					array.add(fld);
				}
				else
				{
					throw new Throwable( "标记未正确关闭！" );
				}
				pos = pos2 + 1;
			}
			else
			{
				pos = -1;
			}
		}
		return array;
		
	}
	
	public Object keyIsNull(Recordset recordset,String key) throws Throwable{
		return (recordset.containsField(key) && !recordset.isNull(key)) ? recordset.getValue(key) : null;
	}
}
