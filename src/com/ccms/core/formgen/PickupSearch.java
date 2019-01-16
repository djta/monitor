package com.ccms.core.formgen;

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
public class PickupSearch extends GenericTransaction
{

	//define el ID del recordset a publicar
	//String _rsName = "query.sql";     //get rsname from config.xml
	
	//ghost @Override
	public int service(Recordset inputs) throws Throwable
	{

		//reutiliza la logica de la clase padre
		int rc = super.service(inputs);

		String sqlFile = getConfig().getConfigValue("sql-template","query-base.sql");        //get base sql template
		String pagingRecordsetName = getConfig().getConfigValue("paging-recordset","query.sql");    //get paging recordset name

		String qBase = getResource(sqlFile);

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
		if(table==null || field==null || field_alias==null)
		{
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
		
		//ahora reemplaza los valores de los parametros en el query
		String sql = getSQL(qBase, inputs);
			
		//ejecutar query y crear recordset
		Recordset rs = getDb().get(sql);

		//retorno data?
		if (rs.getRecordCount()>0)
		{
			//publicar recordset
			getSession().setAttribute(pagingRecordsetName, rs);
			rc = 0;
		}
		else
		{
			//publicar recordset
			//rs.addNew();        /*add a blank row*/
			getSession().setAttribute(pagingRecordsetName, rs);
			rc = 1;
		}
		
		return rc;
		
	}

}
