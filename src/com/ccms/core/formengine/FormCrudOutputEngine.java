package com.ccms.core.formengine;


import com.ccms.Constants;
import com.ccms.caches.CacheProvider;
import com.ccms.core.foctory.FormBean;

import dinamica.GenericOutput;
import dinamica.GenericTransaction;
import dinamica.TemplateEngine;

/**
 * @author zhangchuan
 * 输出表单界面
 *
 */
public class FormCrudOutputEngine extends GenericOutput
{

    public void print(TemplateEngine te, GenericTransaction data)
        throws Throwable
    {
    	
		String formId = getConfig().getConfigValue("form-id", "form_id");
		CacheProvider cp = new FormProviderImpl();
		FormBean form = cp.getFormBeanById(Integer.parseInt(getRequest().getParameter(formId)));

		String locale = getSession().getAttribute(Constants.DINAMICA_USER_LOCALE).toString();
		if(form.getSearch_action().equals("0")){
			if(form.getForm_action().equals("0")){
				te.replace("${controls_editor}", "");
				te.replace("${controls_filter}", "");
			}else{
				te.replace("${controls_editor}", "");
				te.replace("${controls_filter}", "en".equalsIgnoreCase(locale)?form.getFormControlsEn():form.getFormControlsCn());
			}
		}else{
			if(form.getForm_action().equals("0")){
				te.replace("${controls_editor}", "");
			}else{
				te.replace("${controls_editor}", "en".equalsIgnoreCase(locale)?form.getFormControlsEn():form.getFormControlsCn());
			}
			te.replace("${controls_filter}", "en".equalsIgnoreCase(locale)?form.getFilterControlsEn():form.getFilterControlsCn());
			te.replace("${view_title}", "en".equalsIgnoreCase(locale)?form.getViewTitleEn():form.getViewTitleCn());
			te.replace("${view_field}", form.getViewField());
		}
		
		super.print(te, data);
	}
}
