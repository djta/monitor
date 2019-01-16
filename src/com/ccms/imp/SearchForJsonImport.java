package com.ccms.imp;



import java.math.BigDecimal;
import java.sql.Types;
import java.util.HashMap;
import java.util.Iterator;

import dinamica.GenericTransaction;
import dinamica.Recordset;
import dinamica.RecordsetField;


/**
 * 利用easyui分页类
 * @author zhangchuan
 * @email zhangchuanhz@gmail.com
 * @date 2013-12-20 下午04:35:46
 */
public class SearchForJsonImport extends GenericTransaction
{

	public int service(Recordset inputs) throws Throwable
	{

		int rc = super.service(inputs);
		String pagingName = getConfig().getConfigValue("paging-recordset","query.sql");
		String totalName = getConfig().getConfigValue("total-recordset","query-page.sql");
		String pageSizeStr = getConfig().getConfigValue("paging-pagesize","10");
		String sessionRecordsetName = getConfig().getConfigValue("session-recordset","query-session.sql");
		
		Recordset sessionRecordset=(Recordset)getSession().getAttribute(sessionRecordsetName);
		if(null==sessionRecordset){
			throw new Throwable("Session Recordset 不能为空");
		}

		
		//分页排序相关
		String page = getRequest().getParameter("pageNo");
		
		Integer currPage = 1;
		Integer pageSize = 10;
		if(page != null && page.length() > 0){
			try{
				currPage = Integer.parseInt(page);
			}catch(Throwable a){
				
			}
		}
		if(pageSizeStr != null && pageSizeStr.length() > 0){
			try{
				pageSize = Integer.parseInt(pageSizeStr);
			}catch(Throwable a){
				
			}
		}
		
		
		Integer _total = sessionRecordset.getRecordCount();
		Recordset rsPage = new Recordset();
		rsPage.append("total", Types.INTEGER);
		rsPage.append("pageno", Types.INTEGER);
		rsPage.append("pages", Types.INTEGER);
		rsPage.addNew();
		BigDecimal b1 = new BigDecimal(_total);
		BigDecimal b2 = new BigDecimal(pageSize);
		Integer _pageCount = b1.divide(b2,java.math.BigDecimal.ROUND_UP).intValue();
		
		sessionRecordset.setPageSize(pageSize);
		rsPage.setValue("total", _total);
		rsPage.setValue("pageno", currPage);
		rsPage.setValue("pages", _pageCount);
		
	
		Recordset rs=sessionRecordset.getPage(currPage);
		publish(pagingName, rs);
		publish(totalName, rsPage);
		
		return rc;
		
	}

}
