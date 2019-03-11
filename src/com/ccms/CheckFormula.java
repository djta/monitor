package com.ccms;

import dinamica.Db;
import dinamica.GenericTableManager;
import dinamica.Recordset;
import dinamica.StringUtil;

public class CheckFormula extends GenericTableManager{
	public int service(Recordset inputParams) throws Throwable
	{
		int rc = 0;
		super.service(inputParams);
		Db db = getDb();
		String formula_value=inputParams.getString("formula_value");

		String checkSql=StringUtil.replace(getSQL(getResource("query.sql"), inputParams), "${formula_value_real}", formula_value);
		Recordset detailRecordset=new Recordset();
		detailRecordset.append("check_result", java.sql.Types.VARCHAR);
		try {
			db.get(checkSql);
			detailRecordset.addNew();
			detailRecordset.setValue("check_result", "success");
		}catch (Throwable e) {
			// TODO: handle exception
			detailRecordset.addNew();
			detailRecordset.setValue("check_result", "fail");
		}
		publish("check", detailRecordset);
		return rc;
		
	}
}
