package com.ccms.core.formengine;


import com.ccms.caches.CacheConst;
import com.ccms.caches.CacheFactory;
import com.ccms.caches.CacheProvider;
import com.ccms.caches.CacheService;
import com.ccms.core.foctory.DocumentBean;
import com.ccms.core.foctory.FormBean;

public final class FormProviderImpl implements CacheProvider {

	public FormBean getFormBeanById(int id) {
		CacheFactory factory = CacheFactory.getInstance();
		FormBean formBean = factory.getValue(CacheConst.FORM_CACHE_PREFIX + id);
		if(formBean == null){//如果初次没取到，则重新加载一次
			CacheService service = new FormServiceImpl();
			formBean = service.loadForm(id);
		}
		return formBean;
	}

	public DocumentBean getDocumentBeanById(int id) {
		CacheFactory factory = CacheFactory.getInstance();
		DocumentBean docBean = factory.getValue(CacheConst.DOCUMENT_CACHE_PREFIX + id);
		if(docBean == null){//如果初次没取到，则重新加载一次
			CacheService service = new FormServiceImpl();
			docBean = service.loadDocument(id);
		}
		return docBean;
	}

}
