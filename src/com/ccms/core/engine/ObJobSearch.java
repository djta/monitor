package com.ccms.core.engine;

import java.math.BigDecimal;
import java.sql.Types;

import com.ccms.dialect.Dialect;
import com.ccms.dialect.DialectFactory;

import dinamica.Db;
import dinamica.GenericTransaction;
import dinamica.Recordset;
import dinamica.StringUtil;

/**
 * Motor de busqueda, construye un SQL condicionalmente
 * de acuerdo a los parametros recibidos, ejecuta el query
 * y retorna 0 o 1 dependiendo de si el recordset tiene o no
 * registros. Esta clase dejara el recordset en sesion para que
 * pueda mostrarse en una vista paginada.
 * <br><br>
 * Actualizado: 2007-06-27<br>
 * Framework Dinamica - Distribuido bajo licencia LGPL<br>
 * @author mcordova (martin.cordova@gmail.com)
 * */
public class ObJobSearch extends GenericTransaction
{

	//define el ID del recordset a publicar
	//String _rsName = "query.sql";     //get rsname from config.xml
	
	//ghost @Override
	public int service(Recordset inputs) throws Throwable
	{

		//reutiliza la logica de la clase padre
		int rc = super.service(inputs);

		Db db = getDb();

		String sqlFile = getConfig().getConfigValue("sql-template","query-base.sql");
		String pagingName = getConfig().getConfigValue("paging-recordset","query.sql");
		String totalName = getConfig().getConfigValue("total-recordset","query-page.sql");
		
		String addonSearchSql = getSQL(getResource("query_search_sql.sql"), inputs);
		Recordset rsSearchSqld = db.get(addonSearchSql);
		rsSearchSqld.first();
		String search_sql = rsSearchSqld.getString("search_sql");
		Integer job_quota = rsSearchSqld.getInteger("job_quota");
		
		String qBase = getResource(sqlFile);
		if(!(search_sql==null || "".equals(search_sql)))
			qBase = search_sql;

		String qfieldGrid = getSQL(getResource("query_grid_field.sql"), inputs);
		Recordset rsFieldGrid = db.get(qfieldGrid);

		String qtable = getSQL(getResource("query_table.sql"), inputs);
		Recordset rsTable = db.get(qtable);

		rsTable.first();
		String table = rsTable.getString("table_code");
		String cust_code_field = rsTable.getString("cust_code_field");
		String fields = buildGridArrayRecordset(rsFieldGrid);

		qBase = StringUtil.replace(qBase, "${table}", table);
		qBase = StringUtil.replace(qBase, "${field}", fields);
		
		String queryFirst = getSQL(getResource("query_filter_first.sql"), inputs);
		String queryChildren = getSQL(getResource("query_filter_children.sql"), inputs);
		String filterString = SearchClauseUtil.getFilters(db, queryFirst, queryChildren, table, cust_code_field);

		//分页排序相关
		String page = getRequest().getParameter("pageNo");
		String sort = getRequest().getParameter("sort");
		String order = getRequest().getParameter("order");
		String pageSizeStr = getRequest().getParameter("pageSize");
		
		String orderby = "";
		Integer currPage = 1;
		Integer pageSize = 10;
		if(page != null && page.length() > 0){
			try{
				currPage = Integer.parseInt(page);
			}catch(Throwable a){
				
			}
		}
		if(pageSizeStr != null && pageSizeStr.length() > 0){
			try{
				pageSize = Integer.parseInt(pageSizeStr);
				if(pageSize > 500){
					pageSize = 500;
				}
			}catch(Throwable a){
				
			}
		}
		if(sort != null && sort.length() > 0){
			orderby = " order by " + sort;
			if(order != null && order.length() > 0){
				orderby += " " + order;
			}
		}
		
		qBase = StringUtil.replace(qBase, "${filter}", filterString);

		String sql = getSQL(qBase, inputs);
		sql = StringUtil.replace(sql, "${orderby}", orderby);
		
		//限制查询条数
		Dialect dialect = DialectFactory.buildDialect(getConnection().getMetaData().getDatabaseProductName().toLowerCase());
		sql = dialect.getLimitString(sql, pageSize*(currPage-1), pageSize);
		Recordset rs = getDb().get(sql);
		
		String queryCount = "select count(1) as record_total, count(distinct " + cust_code_field + ") as cust_total  from (${table}) t ";
		queryCount = StringUtil.replace(queryCount, "${table}", StringUtil.replace(qBase, "${orderby}", ""));
		Recordset rsCount = getDb().get(queryCount);
		rsCount.first();
		Integer _total = rsCount.getInteger("record_total");
		if( null != job_quota && _total > job_quota ){
			_total = job_quota;
		}
		Integer _custTotal = rsCount.getInteger("cust_total");
		if( rs.getRecordCount() > 0 ){
			rs.last();
			rs.setValue("cust_count", _custTotal);
			rs.top();
		}
		
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
		
		getRequest().getSession().setAttribute(pagingName, rs);
		publish(pagingName, rs);
		publish(totalName, rsPage);

		return rc;
	}

	String buildGridArrayRecordset (Recordset rs) throws Throwable {
		String field = ""; 
		rs.top();
		while (rs.next()){
			field = field + rs.getString("field_code") + " as "+rs.getString("colname")+",";
		}
		if (field.endsWith(","))
			field = field.substring(0,field.length()-1);
		
		return field;			
	}

}
