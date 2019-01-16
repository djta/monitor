package com.ccms.dinamica.domain.utils;

import dinamica.*;

/**
 * Publica un recordset que contiene el nombre
 * del action para la base de datos usada<br><br>
 * Fecha de creacion: 2010-08-06<br>
 * Fecha de actualizacion: 2010-08-06<br>
 * @author Martin Cordova y Asociados C.A
 */
public class DBType extends GenericTransaction {

	@Override
	public int service(Recordset inputParams) throws Throwable {
		
		int rc = super.service(inputParams);
		
		Recordset rs = new Recordset();
		rs.append("action", java.sql.Types.VARCHAR);
		rs.addNew();
		
		String dbName = this.getConnection().getMetaData().getDatabaseProductName().toLowerCase();
		if (dbName.contains("oracle"))
			rs.setValue("action", "generateoracle");
		else
			rs.setValue("action", "generatepgsql");
		
		publish("dbtype", rs);
		
		return rc;
	}

}
