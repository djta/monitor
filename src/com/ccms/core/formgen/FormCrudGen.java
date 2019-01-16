package com.ccms.core.formgen;

import java.sql.Types;

import com.ccms.caches.impl.CacheProviderImpl;
import com.ccms.core.foctory.FormBean;

import dinamica.GenericTransaction;
import dinamica.Recordset;

public class FormCrudGen extends GenericTransaction {

	@Override
	public int service(Recordset inputParams) throws Throwable {
		int rc = super.service(inputParams);

		String curdParamsId = getConfig().getConfigValue("crud-params-id","crud_params");
		String suffix = getConfig().getConfigValue("suffix-col","document_id");
		String saveCols = getConfig().getConfigValue("save-cols","");
		String filterRecordset = getConfig().getConfigValue("filter-recordset","filter_recordset");
		String showRecordset = getConfig().getConfigValue("show-recordset","show_recordset");
		String cascadeRecordset = getConfig().getConfigValue("cascade-recordset","query_cascade_field.sql");
		String formRecordset = getConfig().getConfigValue("form-recordset","query-form.sql");
		
		Integer form_id = inputParams.getInteger("form_id");
		//每次都从缓存中重新复制一份
		FormBean form = new CacheProviderImpl().getFormBeanById(form_id);
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
		
		String[] cols = saveCols.split(",");
		
		Recordset sessionRecord = new Recordset();
		for(int i=0;i<cols.length;i++){
			sessionRecord.append(cols[i], inputParams.getField(cols[i]).getType());
		}
		sessionRecord.addNew();
		for(int i=0;i<cols.length;i++){
			sessionRecord.setValue(cols[i], inputParams.getValue(cols[i]));
		}
		
		String suffixValue = inputParams.getString(suffix);
		
		getRequest().getSession().setAttribute(curdParamsId+"_"+suffixValue, sessionRecord);
		
		//将请求过来的查询数据存下来，赋值到查询条件里查询
		publish(filterRecordset, rsFilter);
		//将请求过来的查询数据存下来，赋值到表单界面
		publish(showRecordset, rsShow);
		//级联字段存储
		publish(cascadeRecordset, rsFieldCascade);
		//form数据
		publish(formRecordset, rsForm);
		publish("_request", rsNewInputParames);
		
		return rc;
	}
}
