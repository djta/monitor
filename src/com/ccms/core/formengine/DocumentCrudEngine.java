package com.ccms.core.formengine;

import com.ccms.core.foctory.DocumentBean;

import dinamica.GenericTransaction;
import dinamica.Recordset;

public class DocumentCrudEngine extends GenericTransaction {

	@Override
	public int service(Recordset inputParams) throws Throwable {
		int rc = super.service(inputParams);

		Integer documentId = inputParams.getInteger("document_id");
		DocumentBean document = new FormProviderImpl().getDocumentBeanById(documentId);
		Recordset rsDocument = document.getRsDocument().copy();
		Recordset rsField = document.getRsDocumentParams().copy();
		StringBuffer sb = new StringBuffer(256);
		rsField.top();
		while(rsField.next()){
			String field_code = rsField.getString("params_url");
			if(field_code == null || field_code.length() <= 0){
				continue;
			}
			String field_value = getRequest().getParameter(field_code);
			if(field_value != null && field_value.length() > 0){
				rsField.setValue("params_value", field_value);
				sb.append("&").append(field_code).append("=").append(field_value);
			}
		}
		String filterParam = sb.toString();
		//处理上下左右URL，后面拼接过滤条件和必要的传递参数
		String requriedParam = "&__p_pk_value__=${fld:__p_pk_value__}&__pk_value__=${fld:__pk_value__}";
		String nav_url_top = rsDocument.getString("nav_url_top");
		if(nav_url_top != null && nav_url_top.length() > 0){
			if(nav_url_top.indexOf(requriedParam)<0){
				sb.delete(0, sb.length());
				if(nav_url_top.indexOf("?") > 0){
					sb.append(nav_url_top);
				}else{
					sb.append(nav_url_top).append("?1=1");
				}
					sb.append(requriedParam).append(filterParam);
				rsDocument.setValue("nav_url_top", sb.toString());
			}
		}
		String nav_url_bottom = rsDocument.getString("nav_url_bottom");
		if(nav_url_bottom != null && nav_url_bottom.length() > 0){
			if(nav_url_bottom.indexOf(requriedParam)<0){
				sb.delete(0, sb.length());
				if(nav_url_bottom.indexOf("?") > 0){
					sb.append(nav_url_bottom);
				}else{
					sb.append(nav_url_bottom).append("?1=1");
				}
				sb.append(requriedParam).append(filterParam);
				rsDocument.setValue("nav_url_bottom", sb.toString());
			}
		}
		String nav_url = rsDocument.getString("nav_url");
		if(nav_url != null && nav_url.length() > 0){
			if(nav_url.indexOf(requriedParam)<0){
				sb.delete(0, sb.length());
				if(nav_url.indexOf("?") > 0){
					sb.append(nav_url);
				}else{
					sb.append(nav_url).append("?1=1");
				}
				sb.append(requriedParam).append(filterParam);
				rsDocument.setValue("nav_url", sb.toString());
			}
		}
		String nav_url_right = rsDocument.getString("nav_url_right");
		if(nav_url_right != null && nav_url_right.length() > 0){
			if(nav_url_right.indexOf(requriedParam)<0){
				sb.delete(0, sb.length());
				if(nav_url_right.indexOf("?") > 0){
					sb.append(nav_url_right);
				}else{
					sb.append(nav_url_right).append("?1=1");
				}
				sb.append(requriedParam).append(filterParam);
				rsDocument.setValue("nav_url_right", sb.toString());
			}
		}
		
		publish("query_filter_field.sql", rsField);
		publish("query-document.sql", rsDocument);
		
		return rc;
	}
}
