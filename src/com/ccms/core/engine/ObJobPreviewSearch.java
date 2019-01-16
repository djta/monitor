package com.ccms.core.engine;

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
public class ObJobPreviewSearch extends GenericTransaction
{

	//define el ID del recordset a publicar
	//String _rsName = "query.sql";     //get rsname from config.xml
	
	//ghost @Override
	public int service(Recordset inputs) throws Throwable
	{

		//reutiliza la logica de la clase padre
		int rc = super.service(inputs);

		Db db = getDb();

		//String colName = getConfig().getConfigValue("colname");             //get filter field  如果只有一个标签,简单.
		//String operatorName = getConfig().getConfigValue("operator");       //get operator var
		String sqlFile = getConfig().getConfigValue("sql-template","query-base.sql");        //get base sql template
		String preview_sql = getConfig().getConfigValue("preview-sql","preview_sql");
		
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
		
		qBase = StringUtil.replace(qBase, "${filter}", filterString);

		String sql = getSQL(qBase, inputs);
		
		//限制查询条数
		Dialect dialect = DialectFactory.buildDialect(getConnection().getMetaData().getDatabaseProductName().toLowerCase());
		sql = dialect.getLimitString(sql, 0, job_quota);

		//输出SQL语句
//		getSession().setAttribute(preview_sql, sql);

		Recordset rsPreview = new Recordset();
		rsPreview.append("preview_sql", Types.VARCHAR);
		rsPreview.addNew();
		rsPreview.setValue("preview_sql", sql);
		publish(preview_sql, rsPreview);
				
		return rc;
	}

	String buildGridArrayRecordset (Recordset rs) throws Throwable
	{
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
