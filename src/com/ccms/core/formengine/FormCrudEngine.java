package com.ccms.core.formengine;

import java.sql.Types;

import com.ccms.Constants;
import com.ccms.core.foctory.FormBean;
import com.ccms.core.foctory.FormFactory;

import dinamica.GenericTransaction;
import dinamica.Recordset;
import dinamica.security.DinamicaUser;

public class FormCrudEngine extends GenericTransaction {

	@Override
	public int service(Recordset inputParams) throws Throwable {
		int rc = super.service(inputParams);

		String formId = getConfig().getConfigValue("form-id", "form_id");
		String filterRecordset = getConfig().getConfigValue("filter-recordset","filter_recordset");
		String showRecordset = getConfig().getConfigValue("show-recordset","show_recordset");
		String cascadeRecordset = getConfig().getConfigValue("cascade-recordset","query_cascade_field.sql");
		String formRecordset = getConfig().getConfigValue("form-recordset","query-form.sql");
		String priviledgeRecordset = getConfig().getConfigValue("priviledge-recordset","query_priviledge.sql");
		
		Integer form_id = inputParams.getInteger(formId);
		//每次都从缓存中重新复制一份
		FormBean form = new FormProviderImpl().getFormBeanById(form_id);
		Recordset rsFieldFilter = form.getQueryFilterField().copy();
		Recordset rsFieldShow = form.getQueryShowField().copy();
		Recordset rsFieldCascade = form.getQueryCascadeField().copy();
		Recordset rsForm = form.getRsForm();

		Recordset rsNewInputParames = inputParams.copyStructure();
		
		Recordset rsFilter = new Recordset();
		rsFilter.append("field_code_alias", Types.VARCHAR);
		rsFilter.append("show_type", Types.VARCHAR);
		rsFilter.append("field_value", Types.VARCHAR);
		
		rsFieldFilter.top();
		while(rsFieldFilter.next()){
			String field_code = rsFieldFilter.getString("field_code_alias");
			String field_value = getRequest().getParameter(field_code);
			if(field_value != null && field_value.length() > 0){
				rsFilter.addNew();
				rsFilter.setValue("field_code_alias", field_code);
				rsFilter.setValue("show_type", rsFieldFilter.getString("show_type"));
				rsFilter.setValue("field_value", field_value);
				
				if(!rsNewInputParames.containsField(field_code)){
					rsNewInputParames.append(field_code, rsFieldFilter.getString("show_type").equalsIgnoreCase("integer")?Types.INTEGER:Types.VARCHAR);
				}
			}
		}
		

		Recordset rsShow = new Recordset();
		rsShow.append("field_code_alias", Types.VARCHAR);
		rsShow.append("show_type", Types.VARCHAR);
		rsShow.append("field_value", Types.VARCHAR);
		
		rsFieldShow.top();
		while(rsFieldShow.next()){
			String field_code = rsFieldShow.getString("field_code_alias");
			String field_value = getRequest().getParameter(field_code);
			if(field_value != null && field_value.length() > 0){
				rsShow.addNew();
				rsShow.setValue("field_code_alias", field_code);
				rsShow.setValue("show_type", rsFieldShow.getString("show_type"));
				rsShow.setValue("field_value", field_value);

				if(!rsNewInputParames.containsField(field_code)){
					rsNewInputParames.append(field_code, rsFieldFilter.getString("show_type").equalsIgnoreCase("integer")?Types.INTEGER:Types.VARCHAR);
				}
			}
		}
		
		rsNewInputParames.addNew();
		inputParams.copyValues(rsNewInputParames);

		rsFieldFilter.top();
		while(rsFieldFilter.next()){
			String field_code = rsFieldFilter.getString("field_code_alias");
			String field_value = getRequest().getParameter(field_code);
			if(field_value != null && field_value.length() > 0){
				rsNewInputParames.setValue(field_code,rsNewInputParames.getField(field_code).getType()==4/*integer*/?new Integer(field_value):field_value);
			}
		}

		rsFieldShow.top();
		while(rsFieldShow.next()){
			String field_code = rsFieldShow.getString("field_code_alias");
			String field_value = getRequest().getParameter(field_code);
			if(field_value != null && field_value.length() > 0){
				rsNewInputParames.setValue(field_code,rsNewInputParames.getField(field_code).getType()==4/*integer*/?new Integer(field_value):field_value);
			}
		}
		
		//将请求过来的查询数据存下来，赋值到查询条件里查询
		publish(filterRecordset, rsFilter);
		//将请求过来的查询数据存下来，赋值到表单界面
		publish(showRecordset, rsShow);
		//级联字段存储
		publish(cascadeRecordset, rsFieldCascade);
		
		//跳转逻辑以URL里面的操作为准
		String form_action = inputParams.getString("__form_action__");
		String search_action = inputParams.getString("__search_action__");
		if(form_action == null || form_action.length() == 0){
			rsNewInputParames.setValue("__form_action__", Integer.parseInt(form.getForm_action()));
		}
		if(search_action == null || search_action.length() == 0){
			rsNewInputParames.setValue("__search_action__", Integer.parseInt(form.getSearch_action()));
		}
		
		//form数据
		publish(formRecordset, rsForm);
		publish("newInputParames", rsNewInputParames);
		
		//操作权限相关
		Recordset rsPriviledge = new Recordset();
		rsPriviledge.append("is_can_create", Types.VARCHAR);
		rsPriviledge.append("is_can_show", Types.VARCHAR);
		rsPriviledge.append("is_can_delete", Types.VARCHAR);
		rsPriviledge.append("is_can_update", Types.VARCHAR);
		rsPriviledge.append("is_can_query", Types.VARCHAR);
		rsPriviledge.append("is_can_page_size", Types.VARCHAR);
		rsPriviledge.append("is_can_excel", Types.VARCHAR);
		rsPriviledge.append("is_can_pdf", Types.VARCHAR);
		rsPriviledge.append("is_can_attachment", Types.VARCHAR);
		//弹出框大小修改
		rsPriviledge.append("modal_width", Types.VARCHAR);
		rsPriviledge.addNew();
		DinamicaUser user = (DinamicaUser)getSession().getAttribute(Constants.DINAMICA_SECURITY_LOGIN);
		if(FormFactory.hasPriviledge(form, user, Constants.PRIVILEDGE_DELETE)){
			rsPriviledge.setValue("is_can_delete", "inline");
		}else{
			rsPriviledge.setValue("is_can_delete", "none");
		}
		if(FormFactory.hasPriviledge(form, user, Constants.PRIVILEDGE_ADD) || FormFactory.hasPriviledge(form, user, Constants.PRIVILEDGE_UPDATE)){
			rsPriviledge.setValue("is_can_update", "inline");
		}else{
			rsPriviledge.setValue("is_can_update", "none");
		}
		if(FormFactory.hasPriviledge(form, user, Constants.PRIVILEDGE_ADD)){
			rsPriviledge.setValue("is_can_create", "inline");
		}else{
			rsPriviledge.setValue("is_can_create", "none");
		}
		if(FormFactory.hasPriviledge(form, user, Constants.PRIVILEDGE_DETAIL) || FormFactory.hasPriviledge(form, user, Constants.PRIVILEDGE_UPDATE)){
			rsPriviledge.setValue("is_can_show", "inline");
		}else{
			rsPriviledge.setValue("is_can_show", "none");
		}
		if(FormFactory.hasPriviledge(form, user, Constants.PRIVILEDGE_SEARCH)){
			rsPriviledge.setValue("is_can_query", "inline");
		}else{
			rsPriviledge.setValue("is_can_query", "none");
		}
		if(FormFactory.hasPriviledge(form, user, Constants.PRIVILEDGE_PAGE)){
			rsPriviledge.setValue("is_can_page_size", "inline");
		}else{
			rsPriviledge.setValue("is_can_page_size", "none");
		}
		if(FormFactory.hasPriviledge(form, user, Constants.PRIVILEDGE_EXCEL)){
			rsPriviledge.setValue("is_can_excel", "inline");
		}else{
			rsPriviledge.setValue("is_can_excel", "none");
		}
		if(FormFactory.hasPriviledge(form, user, Constants.PRIVILEDGE_PDF)){
			rsPriviledge.setValue("is_can_pdf", "inline");
		}else{
			rsPriviledge.setValue("is_can_pdf", "none");
		}

		if(FormFactory.hasPriviledge(form, user, Constants.PRIVILEDGE_ATTACHMENT)){
			rsPriviledge.setValue("is_can_attachment", "inline");
		}else{
			rsPriviledge.setValue("is_can_attachment", "none");
		}
		
		if(form.getCol_num_edit() > 1){
			rsPriviledge.setValue("modal_width", "modal-lg");
		}
		
		publish(priviledgeRecordset, rsPriviledge);
		
		return rc;
	}
}
