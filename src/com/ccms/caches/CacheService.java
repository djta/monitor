package com.ccms.caches;

import com.ccms.core.foctory.DocumentBean;
import com.ccms.core.foctory.FormBean;


public interface CacheService {
	
	/**
	 * 缓存销毁
	 */
	void destroy();
	
	/**
	 * 加载表单
	 * @param formId
	 */
	FormBean loadForm(int formId);
	
	/**
	 * 加载文档，同时把相关表单也加载
	 * @param documentId
	 */
	DocumentBean loadDocument(int documentId);
}
