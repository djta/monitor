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
public class GetTables extends GenericTransaction
{

	/* (non-Javadoc)
	 * @see dinamica.GenericTransaction#service(dinamica.Recordset)
	 */
	public int service(Recordset inputs) throws Throwable
	{
		
		int rc = super.service(inputs);
		
		String types[] = {"TABLE"};
			
		DatabaseMetaData md = getConnection().getMetaData();
		ResultSet tables = md.getTables(null, (String)inputs.getValue("schema"), "%", types);
		Recordset rs = new Recordset(tables);
		tables.close();	
		publish("tables", rs);
		return rc;

	}

}
