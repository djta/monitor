package com.ccms.report;

import dinamica.*;
import dinamica.xml.*;

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
public class SearchPickup extends GenericTransaction
{

	//define el ID del recordset a publicar
	//String _rsName = "query.sql";     //get rsname from config.xml
	
	//ghost @Override
	public int service(Recordset inputs) throws Throwable
	{

		//reutiliza la logica de la clase padre
		int rc = super.service(inputs);

		//String colName = getConfig().getConfigValue("colname");             //get filter field  如果只有一个标签,简单.
		//String operatorName = getConfig().getConfigValue("operator");       //get operator var
		String sqlFile = getConfig().getConfigValue("sql-template","query-base.sql");        //get base sql template
		String sqlFileFk = getConfig().getConfigValue("sql-template-fk","query-base-fk.sql");        //外键关联表
		String pagingRecordsetName = getConfig().getConfigValue("paging-recordset","query.sql");    //get paging recordset name
		String filterClauseName = getConfig().getConfigValue("filter-clause","filter.clause");    //get filter clause 
		//如果命名空间为空,则取外键
		String strNamespace = inputs.getString("namespace");

		String qBase = getResource(sqlFile);
		
		StringBuffer qFilter = new StringBuffer();

		//get config object
		Config c = getConfig();
		//read confir parameters
		//从config.xml里取多个相同的标签
		Document doc = c.getDocument();
		//read xml - search for all <colname> elements in config.xml
		//取列名
		Element elColName[] = doc.getElements("colname");
		//read xml - search for all <operator> elements in config.xml
		//取操作符名
		Element elOperatorName[] = doc.getElements("operator");

		if (elColName!=null)
		{
			//execute every query
			for (int i = 0; i < elColName.length; i++) 
			{
				//read column name
				Element e = elColName[i];
				String colName = e.getString();

        		//logica generica para generar las clausulas 
        		//all the applicable clauses
                //replace colum fields value.
                //如果单个列名里,有逗号分隔的多个字段,则拆分
        		if(colName != null) {           //update by Oasahi
            		String[] params = colName.split(",");
            
            		for (int j=0;j<params.length;j++)
            		{
            			if (inputs.getValue(params[j])!=null)
            				if(strNamespace==null || "null".equals(strNamespace) || "".equals(strNamespace)){	//如果命名空间为空,则取外键模版
                				qFilter.append(getResource("clause-fk-" + params[j]+ ".sql"));
            				}else{
                				qFilter.append(getResource("clause-" + params[j]+ ".sql"));
            				}

            		}
            	}
			}
		}

		//ya tenemos la lista de condiciones
		String where = qFilter.toString();

		if (elOperatorName!=null)
		{
			//execute every query
			for (int i = 0; i < elOperatorName.length; i++) 
			{
				
				//read operator name
				Element e = elOperatorName[i];
				String operatorName = e.getString();

                //replace operator fields value.    update by Oasahi
                if (operatorName != null) {
            		String[] operators = operatorName.split(",");
            		for (int j=0;j<operators.length;j++)
            		{
            			if (inputs.getValue(operators[j])!=null)
                    		where = StringUtil.replace(where,"${"+operators[j]+"}", inputs.getString(operators[j]));
            		}
            	}
			}
		}

		//替换条件
		qBase = StringUtil.replace(qBase,"${filter}", where);

        //把过波条件保存在进程中,以方便其它地方调用
		getSession().setAttribute(filterClauseName, getSQL(where, inputs));


		if(strNamespace==null || "null".equals(strNamespace) || "".equals(strNamespace)){	//如果命名空间为空,则取外键模版
			qBase = getResource(sqlFileFk);

			String qfield = getSQL(getResource("query-field.sql"), inputs);
			Recordset rsField = getDb().get(qfield);

			rsField.first();
			String schema = rsField.getString("schema_code");
			String table = rsField.getString("table_code");
			String field = rsField.getString("field_code");
			String field_alias = rsField.getString("field_alias");
			String fk_references = rsField.getString("fk_references");
			//外键配置数据不完整
			if(table==null || field==null || field_alias==null ||fk_references==null)
			{
					rc = 1;
					return rc;
			}

			qBase = StringUtil.replace(qBase,"${filter}", where);
			
			qBase = StringUtil.replace(qBase, "${schema}", schema);
			qBase = StringUtil.replace(qBase, "${table}", table);
			qBase = StringUtil.replace(qBase, "${field_code}", field);
			qBase = StringUtil.replace(qBase, "${field_alias}", field_alias);
			qBase = StringUtil.replace(qBase, "${field_reference}", fk_references);
		}

		
		String sql = getSQL(qBase, inputs);
		//查询最大条数
		String limitedcount = getRequest().getParameter("limitedcount");
		int maxCount = 1000;
		if(limitedcount != null && !"".equals(limitedcount)){
			try{
				maxCount = Integer.parseInt(limitedcount);
			}catch(NumberFormatException ne){
				
			}
		}
		//ejecutar query y crear recordset
		Recordset rs = getDb().get(sql,maxCount);

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
