package com.ccms.caches;

import com.ccms.core.foctory.DocumentBean;
import com.ccms.core.foctory.FormBean;


 /**
  * 提供缓存
 */
public interface CacheProvider {
	
	/**
	 * 根据ID获取formbean
	 * @param id
	 * @return
	 */
	public FormBean getFormBeanById(int id);
	
	/** 根据ID获取documentBean
	 * @param id
	 * @return
	 */
	public DocumentBean getDocumentBeanById(int id);
}
