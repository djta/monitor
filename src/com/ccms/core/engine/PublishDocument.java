package com.ccms.core.engine;

import com.ccms.caches.CacheService;
import com.ccms.core.formengine.FormServiceImpl;

import dinamica.GenericTransaction;
import dinamica.Recordset;

/**
 * @author zhangchuan
 * 将文档发布到缓存池中
 *
 */
public class PublishDocument extends GenericTransaction {

	public int service(Recordset inputParams) throws Throwable {
		
		int rc = super.service(inputParams);
		
		Integer documentId = inputParams.getInteger("document_id");

		CacheService service = new FormServiceImpl();
		service.loadDocument(documentId);
		
		return rc;
	}
	
}
