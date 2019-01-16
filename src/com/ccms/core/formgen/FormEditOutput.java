package com.ccms.core.formgen;


import com.ccms.caches.CacheProvider;
import com.ccms.caches.impl.CacheProviderImpl;
import com.ccms.core.foctory.FormBean;

import dinamica.GenericOutput;
import dinamica.GenericTransaction;
import dinamica.Recordset;
import dinamica.TemplateEngine;

/**
 * @author zhangchuan
 * 输出编辑js
 *
 */
public class FormEditOutput extends GenericOutput {

	public void print(TemplateEngine te, GenericTransaction data) throws Throwable {

		String cascadeTag = getConfig().getConfigValue("cascade-tag", "rows-cascade-edit");
		
		String suffixValue = getRequest().getParameter("form_id");
		CacheProvider cp = new CacheProviderImpl();
		Integer form_id = Integer.parseInt(suffixValue);
		FormBean form = cp.getFormBeanById(form_id);

		//替换级联脚本
		Recordset rsCascade = form.getQueryCascadeField().copy();
		te.replace(rsCascade, "", cascadeTag);
		
		te.replace("${controls}", form.getEdit_exce_js());
		
		super.print(te, data);
	}
}
