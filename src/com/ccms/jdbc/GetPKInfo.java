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
public class GetPKInfo extends GenericTransaction
{

	/* (non-Javadoc)
	 * @see dinamica.GenericTransaction#service(dinamica.Recordset)
	 */
	public int service(Recordset inputs) throws Throwable
	{
		
		int rc = super.service(inputs);
		
		Recordset rsinfo = new Recordset();
		rsinfo.append("table", java.sql.Types.VARCHAR);
		
		String types[] = {"TABLE"};
			
		DatabaseMetaData md = getConnection().getMetaData();
		ResultSet tables = md.getTables(null, inputs.getString("schema"), "%", types);
		Recordset rs = new Recordset(tables);
		tables.close();	
		
		while (rs.next())
		{
			String table = rs.getString("table_name");
			ResultSet pk = md.getPrimaryKeys(null, inputs.getString("schema"), table);
			if (!pk.next())
			{
				rsinfo.addNew();
				rsinfo.setValue("table", table);
			}
			pk.close();
		}
		
		publish("tables", rsinfo);
		
		return rc;

	}

}
