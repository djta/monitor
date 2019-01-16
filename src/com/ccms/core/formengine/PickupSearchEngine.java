package com.ccms.core.formengine;

import java.math.BigDecimal;
import java.sql.Types;

import com.ccms.dialect.Dialect;
import com.ccms.dialect.DialectFactory;

import dinamica.GenericTransaction;
import dinamica.Recordset;
import dinamica.StringUtil;

public class PickupSearchEngine extends GenericTransaction
{

	public int service(Recordset inputs) throws Throwable
	{

		int rc = super.service(inputs);
		
		String sqlFile = getConfig().getConfigValue("sql-template","query-base.sql");        //get base sql template
		String pagingRecordsetName = getConfig().getConfigValue("paging-recordset","query.sql");    //get paging recordset name
		String totalName = getConfig().getConfigValue("total-recordset","query-count.sql");
		String pageSizeStr = getConfig().getConfigValue("paging-pagesize","10");
		
		String qBase = getResource(sqlFile);
		
		//分页排序相关
		String page = inputs.getString("pageNo");
		String sort = inputs.getString("sort");
		String order = inputs.getString("order");
		String __page_size__ = inputs.getString("__page_size__");
		
		String orderby = "";
		Integer currPage = 1;
		Integer pageSize = 10;
		if(page != null && page.length() > 0){
			try{
				currPage = Integer.parseInt(page);
			}catch(Throwable a){
				
			}
		}
		if(__page_size__ != null && __page_size__.length() > 0){
			try{
				pageSize = Integer.parseInt(__page_size__);
			}catch(Throwable a){
				
			}
		}else if(pageSizeStr != null && pageSizeStr.length() > 0){
			try{
				pageSize = Integer.parseInt(pageSizeStr);
			}catch(Throwable a){
				
			}
		}
		if(sort != null && sort.length() > 0){
			orderby = " order by " + sort;
			if(order != null && order.length() > 0){
				orderby += " " + order;
			}
		}
		
		String qfield = getSQL(getResource("query-field.sql"), inputs);
		Recordset rsField = getDb().get(qfield);

		rsField.first();
		String schema = rsField.getString("schema_code");
		String table = rsField.getString("table_code");
		String field = rsField.getString("field_code");
		String field_alias = rsField.getString("field_alias");
		String fk_references = rsField.getString("fk_references");
		String fk_sql = rsField.getString("fk_sql");
		String delete_field = rsField.getString("delete_field");
		
		//外键配置数据不完整
		if(table==null || field==null || field_alias==null){
			rc = 1;
			return rc;
		}
		qBase = StringUtil.replace(qBase, "${field_code}", field);
		qBase = StringUtil.replace(qBase, "${field_alias}", field_alias);
		qBase = StringUtil.replace(qBase, "${field_reference}", fk_references);

		if(fk_sql!=null && !"".equals(fk_sql)){
			fk_sql = StringUtil.replace(fk_sql, "${DEF", "${def");
			String table_alias = "(" + fk_sql + ")  " + table;
			qBase = StringUtil.replace(qBase, "${schema}", "");
			qBase = StringUtil.replace(qBase, "${table}", table_alias);
		}else{
			qBase = StringUtil.replace(qBase, "${schema}", schema+".");
			qBase = StringUtil.replace(qBase, "${table}", table);
		}
		
		//删除字段标志
		if(delete_field != null && delete_field.length() > 0){
			qBase = StringUtil.replace(qBase, "${delete_field}", delete_field);
		}else{
			qBase = StringUtil.replace(qBase, "${delete_field}", "null");
		}
		
		String sql = StringUtil.replace(qBase, "${orderby}", orderby);
		Dialect dialect = DialectFactory.buildDialect(getConnection().getMetaData().getDatabaseProductName().toLowerCase());
		sql = dialect.getLimitString(sql, pageSize*(currPage-1), pageSize);
		sql = getSQL(sql, inputs);
		
		Recordset rs = getDb().get(sql);
		
		String queryCount = "select (count(1)) as record_total from (${table}) t ";
		queryCount = StringUtil.replace(queryCount, "${table}", getSQL(StringUtil.replace(qBase, "${orderby}", ""), inputs));
		
		Recordset rsCount = getDb().get(queryCount);
		rsCount.first();
		Integer _total = rsCount.getInteger("record_total");
		Recordset rsPage = new Recordset();
		rsPage.append("total", Types.INTEGER);
		rsPage.append("pageno", Types.INTEGER);
		rsPage.append("pages", Types.INTEGER);
		rsPage.addNew();
		BigDecimal b1 = new BigDecimal(_total);
		BigDecimal b2 = new BigDecimal(pageSize);
		Integer _pageCount = b1.divide(b2,java.math.BigDecimal.ROUND_UP).intValue();
		rsPage.setValue("total", _total);
		rsPage.setValue("pageno", currPage);
		rsPage.setValue("pages", _pageCount);
		
		publish(pagingRecordsetName, rs);
		publish(totalName, rsPage);
		
		return rc;
	}
}
