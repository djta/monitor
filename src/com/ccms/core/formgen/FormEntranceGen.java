package com.ccms.core.formgen;

import java.sql.Types;

import com.ccms.caches.CacheProvider;
import com.ccms.caches.impl.CacheProviderImpl;
import com.ccms.core.foctory.DocumentBean;
import com.ccms.core.foctory.FormBean;

import dinamica.GenericTransaction;
import dinamica.Recordset;

public class FormEntranceGen extends GenericTransaction {

	@Override
	public int service(Recordset inputParams) throws Throwable {
		int rc = super.service(inputParams);

		//每次都从缓存中重新复制一份
		CacheProvider cp = new CacheProviderImpl();
		Integer document_id = inputParams.getInteger("document_id");
		DocumentBean document = cp.getDocumentBeanById(document_id);
		Integer form_id = document.getForm_id();
		FormBean form = cp.getFormBeanById(form_id);
		
		Recordset rsDocumentParams = document.getRsDocumentParams().copy();
		Recordset rsDocument = document.getRsDocument().copy();
		
		Recordset rsField = form.getQueryFilterField();
		if(rsField != null){
			rsField = form.getQueryFilterField().copy();
		}

		Recordset rsShowField = form.getQueryShowField();
		if(rsShowField != null){
			rsShowField = form.getQueryShowField().copy();
		}
		
		StringBuffer sb = new StringBuffer(256);
		//查询条件里的字段
		if(rsField != null){
			rsField.top();
			while(rsField.next()){
				String field_code = rsField.getString("field_code_alias");
				String field_value = getRequest().getParameter(field_code);
				if(field_value != null && field_value.length() > 0 && rsDocumentParams.findRecord("params_code", field_code)==-1){
					sb.append("&").append(field_code).append("=").append(field_value);
				}
			}
		}

		//编辑界面里的字段
		if(rsShowField != null){
			rsShowField.top();
			while(rsShowField.next()){
				String field_code = rsShowField.getString("field_code_alias");
				String field_value = getRequest().getParameter(field_code);
				if(field_value != null && field_value.length() > 0 && rsField.findRecord("field_code_alias", field_code)==-1 && rsDocumentParams.findRecord("params_code", field_code)==-1){
					sb.append("&").append(field_code).append("=").append(field_value);
				}
			}
		}
		
		//条件定义的字段
		rsDocumentParams.top();
		while(rsDocumentParams.next()){
			String field_code = rsDocumentParams.getString("params_url");
			if(field_code == null || field_code.length() <= 0){
				continue;
			}
			String field_value = getRequest().getParameter(field_code);
			if(field_value != null && field_value.length() > 0){
				rsDocumentParams.setValue("params_value", field_value);
			}
		}
		
		String filterParam = sb.toString();
		Recordset rsFilter = new Recordset();
		rsFilter.append("filter_param", Types.VARCHAR);
		rsFilter.append("form_action", Types.VARCHAR);
		rsFilter.append("search_action", Types.VARCHAR);
		rsFilter.addNew();
		rsFilter.setValue("filter_param", filterParam);
		
		String form_action = inputParams.getString("__form_action__");
		String search_action = inputParams.getString("__search_action__");
		
		rsDocument.first();
		//处理上下左右URL，后面拼接过滤条件和必要的传递参数
		String requriedParam = "&__p_pk_value__=${fld:__p_pk_value__}&__pk_value__=${fld:__pk_value__}";
		//以URL里面的操作为准
		if(form_action != null && form_action.length() > 0){
			rsFilter.setValue("form_action", form_action);
			
			requriedParam += "&__parent_form_action__=" + form_action;
		}else{
			rsFilter.setValue("form_action", form.getForm_action());
		}
		if(search_action != null && search_action.length() > 0){
			rsFilter.setValue("search_action", search_action);
		}else{
			rsFilter.setValue("search_action", form.getSearch_action());
		}
		
		String nav_url_top = rsDocument.getString("nav_url_top");
		if(nav_url_top != null && nav_url_top.length() > 0){
			sb.delete(0, sb.length());
			sb.append(getRequest().getContextPath());
			if(nav_url_top.indexOf("?") > 0){
				sb.append(nav_url_top);
			}else{
				sb.append("?1=1").append(nav_url_top);
			}
			sb.append(requriedParam).append(filterParam);
			rsDocument.setValue("nav_url_top", sb.toString());
		}
		String nav_url_bottom = rsDocument.getString("nav_url_bottom");
		if(nav_url_bottom != null && nav_url_bottom.length() > 0){
			sb.delete(0, sb.length());
			sb.append(getRequest().getContextPath());
			if(nav_url_bottom.indexOf("?") > 0){
				sb.append(nav_url_bottom);
			}else{
				sb.append("?1=1").append(nav_url_bottom);
			}
			sb.append(requriedParam).append(filterParam);
			rsDocument.setValue("nav_url_bottom", sb.toString());
		}
		String nav_url = rsDocument.getString("nav_url");
		if(nav_url != null && nav_url.length() > 0){
			sb.delete(0, sb.length());
			sb.append(getRequest().getContextPath());
			if(nav_url.indexOf("?") > 0){
				sb.append(nav_url);
			}else{
				sb.append("?1=1").append(nav_url);
			}
			sb.append(requriedParam).append(filterParam);
			rsDocument.setValue("nav_url", sb.toString());
		}
		String nav_url_right = rsDocument.getString("nav_url_right");
		if(nav_url_right != null && nav_url_right.length() > 0){
			sb.delete(0, sb.length());
			sb.append(getRequest().getContextPath());
			if(nav_url_right.indexOf("?") > 0){
				sb.append(nav_url_right);
			}else{
				sb.append("?1=1").append(nav_url_right);
			}
			sb.append(requriedParam).append(filterParam);
			rsDocument.setValue("nav_url_right", sb.toString());
		}
		
		//将入口传过来的参数带到CRUD入口
		publish("filter_param", rsFilter);
		publish("query_filter_field.sql", rsDocumentParams);
		publish("query-document.sql", rsDocument);
		return rc;
	}
}
