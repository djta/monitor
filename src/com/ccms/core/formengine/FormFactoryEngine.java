package com.ccms.core.formengine;

import java.util.Map;

import com.ccms.context.InitializerServlet;
import com.ccms.core.foctory.FormBean;
import com.ccms.core.foctory.FormFactory;
import com.ccms.core.foctory.FormItemBean;
import com.ccms.core.foctory.FormOutput;
import com.ccms.core.foctory.TemplateBean;

import dinamica.Recordset;
import dinamica.StringUtil;
import dinamica.TemplateEngine;

/**
 * @author zhangchuan
 * 表单缓存管理
 *
 */
public class FormFactoryEngine extends FormFactory{

	public void loadFormFormPage(FormBean bean, TemplateBean tmp) throws Throwable{
		if(tmp == null){
			tmp = new FormTemplateEngine(InitializerServlet.getContext());
		}
		FormOutput fop = new FormOutputEngine(getDb());
		String formControlsCn = fop.printPage(bean, tmp, 1, 1);
		String formControlsEn = fop.printPage(bean, tmp, 1, 2);
		Map<Integer, FormItemBean> itemMap = fop.printFormItemPage(bean, tmp, 1, 1);

		//加载级联字段
		String queryCascadeSql = tmp.getQueryCascadeSql();
		queryCascadeSql = StringUtil.replace(queryCascadeSql, "${form_id}", bean.getForm_id().toString());
		Recordset rsField = getDb().get(queryCascadeSql);

		//替换 DEF 标记
		formControlsCn = StringUtil.replace(formControlsCn, "${DEF", "${def");
		formControlsEn = StringUtil.replace(formControlsEn, "${DEF", "${def");
		
		//替换LBL 标记
		formControlsCn = StringUtil.replace(formControlsCn, "${LBL", "${lbl");
		formControlsEn = StringUtil.replace(formControlsEn, "${LBL", "${lbl");
		
		//替换表单模板
		String formFormTpl = tmp.getResource("/WEB-INF/action/ccms/formgen/template/form/"+("0".equals(bean.getSearch_action())?"form.htm":"form_modal.htm"));
		formControlsCn = StringUtil.replace(formFormTpl, "${controls}", formControlsCn);
		formControlsEn = StringUtil.replace(formFormTpl, "${controls}", formControlsEn);
		
		//返回值赋回form
		bean.setFormControlsCn(formControlsCn);
		bean.setFormControlsEn(formControlsEn);
		bean.setQueryCascadeField(rsField);
		bean.setItemMap(itemMap);
	}
	
	public void loadFormFilterPage(FormBean bean, TemplateBean tmp) throws Throwable{
		if(tmp == null){
			tmp = new FormTemplateEngine(InitializerServlet.getContext());
		}
		FormOutput fop = new FormOutputEngine(getDb());
		String filterControlsCn = fop.printPage(bean, tmp, 2, 1);
		String filterControlsEn = fop.printPage(bean, tmp, 2, 2);
		
		//替换 DEF 标记
		filterControlsCn = StringUtil.replace(filterControlsCn, "${DEF", "${def");
		filterControlsEn = StringUtil.replace(filterControlsEn, "${DEF", "${def");
		
		//替换表单模板
		String filterFormTpl = tmp.getResource("/WEB-INF/action/ccms/formgen/template/filter/form.htm");
		filterControlsCn = StringUtil.replace(filterFormTpl, "${controls}", filterControlsCn);
		filterControlsEn = StringUtil.replace(filterFormTpl, "${controls}", filterControlsEn);
		
		//返回值赋回form
		bean.setFilterControlsCn(filterControlsCn);
		bean.setFilterControlsEn(filterControlsEn);
	}
	
	public void loadFormViewPage(FormBean bean, TemplateBean tmp) throws Throwable{
		if(tmp == null){
			tmp = new FormTemplateEngine(InitializerServlet.getContext());
		}
		String viewQueryGridField = tmp.getViewQueryGridField();
		viewQueryGridField = StringUtil.replace(viewQueryGridField, "${form_id}", bean.getForm_id().toString());
		Recordset rsField = getDb().get(viewQueryGridField);
		StringBuffer sTitleCn = new StringBuffer(256);
		StringBuffer sTitleEn = new StringBuffer(256);
		StringBuffer sField = new StringBuffer(256);
		StringBuffer sJson = new StringBuffer(256);
		rsField.top();
		while(rsField.next()){
			if("1".equals(rsField.getString("show_flag"))){
				//设置中文
				rsField.setValue("field_name", rsField.getValue("field_name_cn"));
				TemplateEngine thFieldCn = new TemplateEngine(null, null, tmp.gettTh());
				thFieldCn.replace(rsField,"");
				sTitleCn.append(thFieldCn.toString());
				
				//设置英文
				rsField.setValue("field_name", rsField.getValue("field_name_en"));
				TemplateEngine thFieldEn = new TemplateEngine(null, null, tmp.gettTh());
				thFieldEn.replace(rsField,"");
				sTitleEn.append(thFieldEn.toString());
				
				//输出字段
				TemplateEngine tdField = new TemplateEngine(null, null, tmp.gettTd());
				tdField.replace(rsField,"");
				sField.append(tdField.toString());
			}
			
			//Json输出
			TemplateEngine jsonField = new TemplateEngine(null, null, tmp.gettJson());
			jsonField.replace(rsField,"");
			sJson.append(jsonField.toString());
		}
		//删除最后一个逗号
		if(sJson.length() > 0){
			sJson.deleteCharAt(sJson.length()-1);
		}
		//返回值赋回form
		bean.setViewField(sField.toString());
		bean.setViewTitleCn(sTitleCn.toString());
		bean.setViewTitleEn(sTitleEn.toString());
		bean.setViewJson(sJson.toString());
		//用于汇总列
		bean.setViewQueryGridField(rsField);
	}
}
