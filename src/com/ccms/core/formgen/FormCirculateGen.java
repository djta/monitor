package com.ccms.core.formgen;


import com.ccms.caches.impl.CacheProviderImpl;
import com.ccms.core.foctory.FormBean;

import dinamica.GenericTransaction;
import dinamica.Recordset;
import dinamica.StringUtil;

public class FormCirculateGen extends GenericTransaction {

	@Override
	public int service(Recordset inputParams) throws Throwable {
		int rc = super.service(inputParams);
		
		Integer form_id = inputParams.getInteger("form_id");
		//每次都从缓存中重新复制一份
		FormBean form = new CacheProviderImpl().getFormBeanById(form_id);
		String querySnapshot = getSQL(getResource("query-snapshot.sql"), inputParams);
		querySnapshot = StringUtil.replace(querySnapshot, "${bpk_field}", form.getBpk_field());
		querySnapshot = StringUtil.replace(querySnapshot, "${table}", form.getTable_code());
		
		Recordset rsSnapshot = getDb().get(querySnapshot);
		
		publish("query-snapshot.sql", rsSnapshot);
		
		return rc;
	}
}
