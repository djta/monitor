package com.ccms.report;

import dinamica.GenericTransaction;
import dinamica.Recordset;
import dinamica.StringUtil;

public class SearchComboChild extends GenericTransaction
{

	public int service(Recordset inputs) throws Throwable
	{

		int rc = super.service(inputs);

		String sqlTemplate = getConfig().getConfigValue("sql-template","query-base.sql");
		String qBase = getResource(sqlTemplate);
		
		//取到级联表名
		String table_name = inputs.getString("table_name");
		String parent_field_code = inputs.getString("parent_field_code");
		String show_field_code = inputs.getString("show_field_code");
		String value_field_code = inputs.getString("value_field_code");

		qBase = StringUtil.replace(qBase, "${table_name}", table_name);
		qBase = StringUtil.replace(qBase, "${parent_field_code}", parent_field_code);
		qBase = StringUtil.replace(qBase, "${show_field_code}", show_field_code);
		qBase = StringUtil.replace(qBase, "${value_field_code}", value_field_code);
		
		String sql = getSQL(qBase, inputs);
		Recordset rs = getDb().get(sql);

		publish(sqlTemplate, rs);
		
		return rc;
		
	}

}
