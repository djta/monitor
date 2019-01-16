package com.ccms.core.formgen;

import com.ccms.caches.impl.CacheProviderImpl;
import com.ccms.core.foctory.DocumentBean;

import dinamica.GenericTransaction;
import dinamica.Recordset;

public class DocumentCrudGen extends GenericTransaction {

	@Override
	public int service(Recordset inputParams) throws Throwable {
		int rc = super.service(inputParams);

		Integer documentId = inputParams.getInteger("document_id");
		DocumentBean document = new CacheProviderImpl().getDocumentBeanById(documentId);
		Recordset rsField = document.getRsDocumentParams().copy();
		
		rsField.top();
		while(rsField.next()){
			String field_code = rsField.getString("params_url");
			if(field_code == null || field_code.length() <= 0){
				continue;
			}
			String field_value = getRequest().getParameter(field_code);
			if(field_value != null && field_value.length() > 0){
				rsField.setValue("params_value", field_value);
			}
		}
		
		publish("query_filter_field.sql", rsField);
		publish("query-document.sql", document.getRsDocument());
		
		return rc;
	}
}
