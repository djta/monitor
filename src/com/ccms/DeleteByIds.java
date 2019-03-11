package com.ccms;

import dinamica.Db;
import dinamica.GenericTableManager;
import dinamica.Recordset;
import dinamica.StringUtil;

public class DeleteByIds extends GenericTableManager
{

	public int service(Recordset inputParams) throws Throwable
	{

		int rc = 0;
		super.service(inputParams);
		Db db = getDb();
		String[] ids=inputParams.getString("id").split(",");
		String dsql=getResource("delete.sql");
		for(int i=0;i<ids.length;i++) {
			db.exec(StringUtil.replace(dsql, "${fld:id}", ids[i]));
		}
		return rc;
		
	}


}
