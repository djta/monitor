package com.ccms.report;

import java.sql.Types;

import dinamica.GenericTransaction;
import dinamica.Recordset;

public class ReportCrud extends GenericTransaction {

	@Override
	public int service(Recordset inputParams) throws Throwable {
		int rc = super.service(inputParams);

		String filterValueRecordset = getConfig().getConfigValue("filter-value-recordset","filter_value_recordset");
		String filterField = getConfig().getConfigValue("filter-field","query_filter_field.sql");
		
		Recordset rsField = getDb().get(getSQL(getResource(filterField),inputParams));
		
		Recordset rsFilterValue = new Recordset();
		rsFilterValue.append("field_code_alias", Types.VARCHAR);
		rsFilterValue.append("show_type", Types.VARCHAR);
		rsFilterValue.append("field_value", Types.VARCHAR);
		
		while(rsField.next()){
			String field_code = rsField.getString("field_code_alias");
			String field_value = getRequest().getParameter(field_code);
			if(field_value != null && field_value.length() > 0){
				rsFilterValue.addNew();
				rsFilterValue.setValue("field_code_alias", field_code);
				rsFilterValue.setValue("show_type", rsField.getString("show_type"));
				rsFilterValue.setValue("field_value", field_value);
			}
		}
		//将请求过来的查询数据存下来，赋值到查询条件里查询
		publish(filterValueRecordset, rsFilterValue);
		//查询条件字段
		publish(filterField, rsField);
		
		return rc;
	}
}
