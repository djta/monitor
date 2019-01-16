package com.ccms.project.yanglao;

import dinamica.Db;
import dinamica.GenericTableManager;
import dinamica.Recordset;
import dinamica.StringUtil;

public class InsertJF extends GenericTableManager
{

	public int service(Recordset inputParams) throws Throwable
	{
		int rc = 0;
		super.service(inputParams);
		Db db=getDb();
		String[] userids=inputParams.getString("userids").split(",");
		String insertSql=getResource("insert.sql");
		for (int i = 0; i < userids.length; i++) {
			db.exec(getSQL(StringUtil.replace(insertSql, "${userid}", userids[i]),inputParams));
		}
		return rc;
	} 
}
