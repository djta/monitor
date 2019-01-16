package com.ccms.core.formengine;


import com.ccms.caches.CacheProvider;
import com.ccms.core.foctory.FormBean;

import dinamica.GenericOutput;
import dinamica.GenericTransaction;
import dinamica.TemplateEngine;

/**
 * @author zhangchuan
 * 输出编辑js
 *
 */
public class FormEditOutputEngine extends GenericOutput {

	public void print(TemplateEngine te, GenericTransaction data) throws Throwable {

		String suffixValue = getRequest().getParameter("form_id");
		CacheProvider cp = new FormProviderImpl();
		Integer form_id = Integer.parseInt(suffixValue);
		FormBean form = cp.getFormBeanById(form_id);
		te.replace("${controls}", form.getEdit_exce_js());
		
		super.print(te, data);
	}
}
