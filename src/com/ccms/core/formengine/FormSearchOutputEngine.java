package com.ccms.core.formengine;

import com.ccms.caches.CacheProvider;
import com.ccms.core.foctory.FormBean;

import dinamica.GenericOutput;
import dinamica.GenericTransaction;
import dinamica.Recordset;
import dinamica.TemplateEngine;

public class FormSearchOutputEngine extends GenericOutput {

	public void print(TemplateEngine te, GenericTransaction data) throws Throwable {

		super.print(te, data);

		String pagingRecordsetName = getConfig().getConfigValue("paging-recordset", "query.sql");
		String totalRecordsetName = getConfig().getConfigValue("total-recordset", "query-total.sql");
		String formId = getConfig().getConfigValue("form-id", "form_id");
		String printTag = getConfig().getConfigValue("print-tag", "rows");
		
		CacheProvider cp = new FormProviderImpl();
		FormBean form = cp.getFormBeanById(Integer.parseInt(getRequest().getParameter(formId)));

		te.replace("${json_field}", form.getViewJson());
		
		Recordset rs = data.getRecordset(pagingRecordsetName);
		Recordset rsTotal = data.getRecordset(totalRecordsetName);;
		
		te.replace(rs, "", printTag);
		te.replace(rsTotal, "");
	}
}
