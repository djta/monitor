package com.ccms;

import com.ccms.dialect.Dialect;
import com.ccms.dialect.DialectFactory;

import dinamica.GenericTransaction;
import dinamica.Recordset;
import dinamica.StringUtil;

/**
 * 利用easyui分页类
 * @author zhangchuan
 * @email zhangchuanhz@gmail.com
 * @date 2013-12-20 下午04:35:46
 */
public class PageSearch extends GenericTransaction
{

	public int service(Recordset inputs) throws Throwable
	{

		int rc = super.service(inputs);
		String colname = getConfig().getConfigValue("colname","");
		String sqlFile = getConfig().getConfigValue("sql-template","query-base.sql");
		String pagingName = getConfig().getConfigValue("paging-recordset","query.sql");
		String totalName = getConfig().getConfigValue("total-recordset","query-count.sql");
		String qBase = getResource(sqlFile);
		StringBuffer qFilter = new StringBuffer(256);

		if(colname != null && colname.length() > 0){
			//拼接查询条件
			String[] params = colname.split(",");
			for (int j=0;j<params.length;j++)
			{
				if (inputs.getValue(params[j]) != null){
					qFilter.append(getResource("clause-" + params[j]+ ".sql"));
				}
			}
		}
		
		//分页排序相关
		String page = getRequest().getParameter("page");
		String rows = getRequest().getParameter("rows");
		String sort = getRequest().getParameter("sort");
		String order = getRequest().getParameter("order");
		
		String orderby = "";
		Integer currPage = 1;
		Integer pageSize = 10;
		if(page != null && page.length() > 0){
			try{
				currPage = Integer.parseInt(page);
			}catch(Throwable a){
				
			}
		}
		if(rows != null && rows.length() > 0){
			try{
				pageSize = Integer.parseInt(rows);
			}catch(Throwable a){
				
			}
		}
		if(sort != null && sort.length() > 0){
			orderby = " order by " + sort;
			if(order != null && order.length() > 0){
				orderby += " " + order;
			}
		}
		
		//替换变量
		qBase = StringUtil.replace(qBase,"${filter}", qFilter.toString());
		qBase = getSQL(qBase, inputs);
		
		String sql = StringUtil.replace(qBase, "${orderby}", orderby);
		Dialect dialect = DialectFactory.buildDialect(getConnection().getMetaData().getDatabaseProductName().toLowerCase());
		sql = dialect.getLimitString(sql, pageSize*(currPage-1), pageSize);
		
		String queryCount = "select (count(1)+1) as record_total from (${table}) t ";
		queryCount = StringUtil.replace(queryCount, "${table}", StringUtil.replace(qBase, "${orderby}", ""));
		
		Recordset rs = getDb().get(sql);
		Recordset rsCount = getDb().get(queryCount);
		
		publish(pagingName, rs);
		publish(totalName, rsCount);
		
		return rc;
		
	}

}
