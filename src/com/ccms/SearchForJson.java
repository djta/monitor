package com.ccms;

import java.math.BigDecimal;
import java.sql.Types;

import com.ccms.dialect.Dialect;
import com.ccms.dialect.DialectFactory;

import dinamica.GenericTransaction;
import dinamica.Recordset;
import dinamica.StringUtil;

/**
 * 利用easyui分页类
 * @author zhangchuan
 * @email zhangchuanhz@gmail.com
 * @date 2013-12-20 下午04:35:46
 */
public class SearchForJson extends GenericTransaction
{

	public int service(Recordset inputs) throws Throwable
	{

		int rc = super.service(inputs);
		String colname = getConfig().getConfigValue("colname","");
		String sqlFile = getConfig().getConfigValue("sql-template","query-base.sql");
		String pagingName = getConfig().getConfigValue("paging-recordset","query.sql");
		String totalName = getConfig().getConfigValue("total-recordset","query-page.sql");
		String qBase = getResource(sqlFile);
		StringBuffer qFilter = new StringBuffer(256);

		if(colname != null && colname.length() > 0){
			//拼接查询条件
			String[] params = colname.split(",");
			for (int j=0;j<params.length;j++)
			{
				if (inputs.getValue(params[j]) != null){
					qFilter.append(getResource("clause-" + params[j]+ ".sql"));
				}
			}
		}
		
		//分页排序相关
		String page = getRequest().getParameter("pageNo");
		String sort = getRequest().getParameter("sort");
		String order = getRequest().getParameter("order");
		String pageSizeStr = getRequest().getParameter("pageSize");
		
		String orderby = "";
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
				if(pageSize > 500){
					pageSize = 500;
				}
			}catch(Throwable a){
				
			}
		}
		if(sort != null && sort.length() > 0){
			orderby = " order by ";
			int orderCount = 0;
			// 排序字段分割：多个排序字段用;隔开
			String[] sortArr = sort.split(";");
			order = (order != null && order.length() > 0 ? order : "");
			// 倒序/顺序字段分割
			String[] orderArr = order.split(";");
			// 排序拼接
			for(int i = 0; i < sortArr.length; i++){
				if( null == sortArr[i] || sortArr[i].length() <= 0 ){
					continue;
				}
				String sortStr = sortArr[i];
				if( null != sortStr && sortStr.length() > 0 ){
					if( orderCount == 0 ){
						orderby += sortStr;
					}else{
						orderby += "," + sortStr;
					}
					orderCount++;
					if( orderArr.length >= i+1 && null != orderArr[i] && orderArr[i].length() > 0 ){
						orderby += " " + orderArr[i];
					}
				}
			}
		}
		
		//替换变量
		qBase = StringUtil.replace(qBase,"${filter}", qFilter.toString());
		qBase = getSQL(qBase, inputs);

		String queryCount = "";
		int iCountFrom = 0;
		int iCountTo = 0;
		iCountFrom = qBase.indexOf("${select}");
		iCountTo = qBase.indexOf("${from}");
		if(iCountFrom != -1 && iCountTo != -1){
			queryCount = qBase.substring(0, iCountFrom) + "select count(1) as record_total from " + qBase.substring(iCountTo +"${from}".length());
			queryCount = StringUtil.replace(queryCount,"${orderby}","");
		}else{
			queryCount = "select count(1) as record_total from (${table}) t ";
			queryCount = StringUtil.replace(queryCount, "${table}", StringUtil.replace(qBase, "${orderby}", ""));
		}

		if(iCountFrom != -1 || iCountTo != -1){
			qBase = StringUtil.replace(qBase,"${select}","select");
			qBase = StringUtil.replace(qBase,"${from}","from");
		}
		
		String sql = StringUtil.replace(qBase, "${orderby}", orderby);
		Dialect dialect = DialectFactory.buildDialect(getConnection().getMetaData().getDatabaseProductName().toLowerCase());
		sql = dialect.getLimitString(sql, pageSize*(currPage-1), pageSize);
				
		Recordset rs = getDb().get(sql);
		Recordset rsCount = getDb().get(queryCount);
		rsCount.first();
		Integer _total = rsCount.getInteger("record_total");
		Recordset rsPage = new Recordset();
		rsPage.append("total", Types.INTEGER);
		rsPage.append("pageno", Types.INTEGER);
		rsPage.append("pages", Types.INTEGER);
		rsPage.addNew();
		BigDecimal b1 = new BigDecimal(_total);
		BigDecimal b2 = new BigDecimal(pageSize);
		Integer _pageCount = b1.divide(b2,java.math.BigDecimal.ROUND_UP).intValue();
		rsPage.setValue("total", _total);
		rsPage.setValue("pageno", currPage);
		rsPage.setValue("pages", _pageCount);
		
		publish(pagingName, rs);
		publish(totalName, rsPage);
		
		return rc;
		
	}

}
