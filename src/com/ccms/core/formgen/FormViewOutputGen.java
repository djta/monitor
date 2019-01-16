package com.ccms.core.formgen;

import java.util.Iterator;


import com.ccms.Constants;
import com.ccms.caches.CacheProvider;
import com.ccms.caches.impl.CacheProviderImpl;
import com.ccms.core.foctory.DocumentBean;
import com.ccms.core.foctory.FormBean;

import dinamica.GenericOutput;
import dinamica.GenericTransaction;
import dinamica.Recordset;
import dinamica.TemplateEngine;

public class FormViewOutputGen extends GenericOutput {

	public void print(TemplateEngine te, GenericTransaction data) throws Throwable {

		// reuse superclass code
		super.print(te, data);

		String pagingRecordsetName = getConfig().getConfigValue("paging-recordset", "query.sql");
		String totalRecordsetName = getConfig().getConfigValue("total-recordset", "query-total.sql");
		String curdParamsId = getConfig().getConfigValue("crud-params-id","crud_params");
		String suffix = getConfig().getConfigValue("suffix-col", "uniquegen");
		String printTag = getConfig().getConfigValue("print-tag", "rows");
		
		String suffixValue = getRequest().getParameter(suffix);
		CacheProvider cp = new CacheProviderImpl();
		DocumentBean document = cp.getDocumentBeanById(Integer.parseInt(suffixValue));
		Integer form_id = document.getForm_id();
		FormBean form = cp.getFormBeanById(form_id);

		String locale = getSession().getAttribute(Constants.DINAMICA_USER_LOCALE).toString();
		//替换查询结果的表头和内容
		if("en".equalsIgnoreCase(locale)){
			te.replace("${view_title}", form.getViewTitleEn());
		}else{
			te.replace("${view_title}", form.getViewTitleCn());
		}
		te.replace("${view_field}", form.getViewField());
		
		//打印crud传入参数值
		Recordset rsCrud = data.getRecordset(curdParamsId + "_" + (suffixValue==null?"":suffixValue));
		if(rsCrud != null && rsCrud.getRecordCount() > 0){
			rsCrud.first();
			te.replace(rsCrud ,"");
		}
		
		Recordset rs = (Recordset)getRequest().getSession().getAttribute(pagingRecordsetName+"_"+suffixValue);
		Recordset rsTotal = (Recordset)getRequest().getSession().getAttribute(totalRecordsetName+"_"+suffixValue);
		
		if(rs == null){
			throw new Throwable("未找到数据结果集！");
		}
		
		String pagesize = getRequest().getParameter("pagesize");

        if(rs.getPageCount() > 0)
        	pagesize = String.valueOf(rs.getPageSize());
        if(pagesize != null)
        {
            int i = 0;
            if(rs.getPageCount() == 0)
            	rs.setPageSize(Integer.parseInt(pagesize));
            String s9 = getRequest().getParameter("pagenumber");
            if(s9 == null || s9.equals(""))
            {
                i = rs.getPageNumber();
                if(i == 0)
                    i = 1;
            } else
            {
                i = Integer.parseInt(s9);
            }
            rs = rs.getPage(i);
        }
        
        //将合并列加到结果集中
        if(rsTotal != null){
            rs.addNew();
            rs.last();
            rsTotal.first();
    		Iterator<String> it = rsTotal.getFields().keySet().iterator();
    		while(it.hasNext()){
    			String fld = it.next();
    			if(rs.containsField(fld)){
    				rs.setValue(fld,rsTotal.getDouble(fld));
    			}
    		}
    		//找到第一个非汇总列，并且类型是字符串类型，加个汇总标识
            Recordset rsFirstField = form.getViewQueryGridField();
    		rsFirstField.top();
    		while(rsFirstField.next()){
    			String colName = rsFirstField.getString("colname");
    			String fieldType = rsFirstField.getString("field_type");
    			if(!rsTotal.containsField(colName) && "varchar".equalsIgnoreCase(fieldType)){
    				if("en".equalsIgnoreCase(locale)){
    					rs.setValue(colName, "Total");
    				}else{
    					rs.setValue(colName, "总计");
    				}
    				break;
    			}
    		}
        }
		
		te.setRowEventObject(this);
		te.replace(rs, "", printTag);
	}
}
