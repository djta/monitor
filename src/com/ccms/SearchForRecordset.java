package com.ccms;

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
public class SearchForRecordset extends GenericTransaction
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

		/* ensamblar query */
				//carga el template base del query
		String qBase = getResource(sqlFile);

		//aqui se almacenaran las condiciones del WHERE
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
            				qFilter.append(getResource("clause-" + params[j]+ ".sql"));
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

		//ahora reemplaza las condiciones en el query base
		qBase = StringUtil.replace(qBase,"${filter}", where);

		/* listo el query - quedo ensamblado */
		
		//ahora reemplaza los valores de los parametros en el query
		String sql = getSQL(qBase, inputs);
		//查询最大条数
		String limitedcount = getRequest().getParameter("limitedcount");
		int maxCount = 100000;
		if(limitedcount != null && !"".equals(limitedcount)){
			try{
				maxCount = Integer.parseInt(limitedcount);
			}catch(NumberFormatException ne){
				
			}
		}
		//ejecutar query y crear recordset
		Recordset rs = getDb().get(sql,maxCount);
		
		publish(pagingRecordsetName, rs);
		
		return rc;
		
	}

}
