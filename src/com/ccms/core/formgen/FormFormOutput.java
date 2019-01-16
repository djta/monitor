package com.ccms.core.formgen;


import com.ccms.Constants;
import com.ccms.caches.CacheProvider;
import com.ccms.caches.impl.CacheProviderImpl;
import com.ccms.core.foctory.DocumentBean;
import com.ccms.core.foctory.FormBean;

import dinamica.GenericOutput;
import dinamica.GenericTransaction;
import dinamica.TemplateEngine;

/**
 * @author zhangchuan
 * 输出表单界面
 *
 */
public class FormFormOutput extends GenericOutput
{

    public void print(TemplateEngine te, GenericTransaction data)
        throws Throwable
    {
    	super.print(te, data);
    	
		String suffix = getConfig().getConfigValue("suffix-col", "document_id");
		String suffixValue = getRequest().getParameter(suffix);
		CacheProvider cp = new CacheProviderImpl();
		DocumentBean document = cp.getDocumentBeanById(Integer.parseInt(suffixValue));
		Integer form_id = document.getForm_id();
		FormBean form = cp.getFormBeanById(form_id);

		String locale = getSession().getAttribute(Constants.DINAMICA_USER_LOCALE).toString();
		if("en".equalsIgnoreCase(locale)){
			te.replace("${controls}", form.getFormControlsEn());
		}else{
			te.replace("${controls}", form.getFormControlsCn());
		}
	}
}
