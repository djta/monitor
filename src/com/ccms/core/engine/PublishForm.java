package com.ccms.core.engine;

import com.ccms.caches.CacheService;
import com.ccms.core.formengine.FormServiceImpl;

import dinamica.GenericTransaction;
import dinamica.Recordset;

/**
 * @author zhangchuan
 * 将表单发布到缓存池中
 *
 */
public class PublishForm extends GenericTransaction {

	public int service(Recordset inputParams) throws Throwable {
		
		int rc = super.service(inputParams);
		
		Integer formId = inputParams.getInteger("form_id");
		
		CacheService service = new FormServiceImpl();
		service.loadForm(formId);
		
		return rc;
	}
	
}
