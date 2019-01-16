package com.ccms.jdbc;

import dinamica.*;

import java.sql.*;

/**
 * Base class to program business transaction services (read/write).
 * All transactions will subclass this class.
 * 
 * <br>
 * Creation date: 29/10/2003<br>
 * Last Update: 29/10/2003<br>
 * (c) 2003 Martin Cordova<br>
 * This code is released under the LGPL license<br>
 * @author Martin Cordova
 * */
public class GetColumns extends GenericTransaction
{

	/* (non-Javadoc)
	 * @see dinamica.GenericTransaction#service(dinamica.Recordset)
	 */
	public int service(Recordset inputs) throws Throwable
	{
		
		int rc = super.service(inputs);
		
		DatabaseMetaData md = getConnection().getMetaData();
		// 参数初始化
		String dbType = getContext().getInitParameter("db");
		String schema = "";
		String table = "";
		if(dbType.equalsIgnoreCase("postgresql")){
			schema = (String)inputs.getValue("schema");
			table = (String)inputs.getValue("table");			
		}else if(dbType.equalsIgnoreCase("oracle")){
			schema = (String)inputs.getValue("schema").toString().toUpperCase();
			table = (String)inputs.getValue("table").toString().toUpperCase();
		}else if(dbType.equalsIgnoreCase("sqlserver")){
			schema = "%";
			table = (String)inputs.getValue("table");			
		}else{
			schema = (String)inputs.getValue("schema");
			table = (String)inputs.getValue("table");			
		}

		ResultSet cols = md.getColumns(null, schema, table, "%");
		Recordset rs = new Recordset(cols);
		cols.close();
		//同步各个数据库的字段类型与系统中映射
		while(rs.next()){
			String column_name = rs.getString("column_name");
			String type_name = rs.getString("type_name");
			String system_type = "";
			if (type_name.equalsIgnoreCase("varchar") || type_name.equalsIgnoreCase("varchar2") || type_name.equalsIgnoreCase("char"))
				system_type = "varchar";
			else if (type_name.equalsIgnoreCase("integer") || type_name.equalsIgnoreCase("int4") || type_name.equalsIgnoreCase("int") || type_name.equalsIgnoreCase("number"))
				system_type = "integer";
			else if (type_name.equalsIgnoreCase("double") || type_name.equalsIgnoreCase("numeric"))
				system_type = "double";
			else if (type_name.equalsIgnoreCase("date"))
				system_type = "date";
			else if (type_name.equalsIgnoreCase("timestamp") || type_name.equalsIgnoreCase("datetime"))
				system_type = "timestamp";
			else
				system_type = "varchar";
			rs.setValue("column_name", column_name.toLowerCase());
			rs.setValue("type_name", system_type);
		}
		publish("columns", rs);
		return rc;

	}

}
