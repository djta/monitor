package com.ccms.util.msg;

import dinamica.GenericTransaction;
import dinamica.Recordset;
import dinamica.StringUtil;

public class QueryStaffBySkill extends GenericTransaction {

	public int service(Recordset inputParams) throws Throwable 
	{
		int rc = super.service(inputParams);
		
		String skillId = inputParams.getString("id");
		if(skillId == null || "".equals(skillId)) return rc;
		
		String query = getSQL(getResource("query.sql"),inputParams);
		query = StringUtil.replace(query, "${id}", skillId);
		
		Recordset rs = getDb().get(query);
		
		publish("query.sql",rs);
		
		return rc;
	}
}
