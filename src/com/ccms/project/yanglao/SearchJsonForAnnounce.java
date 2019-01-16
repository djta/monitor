package com.ccms.project.yanglao;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;

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
public class SearchJsonForAnnounce extends GenericTransaction
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
			orderby = " order by " + sort;
			if(order != null && order.length() > 0){
				orderby += " " + order;
			}
		}
		
		//替换变量
		qBase = StringUtil.replace(qBase,"${filter}", qFilter.toString());
		qBase = getSQL(qBase, inputs);
		
		String sql = StringUtil.replace(qBase, "${orderby}", orderby);
		Dialect dialect = DialectFactory.buildDialect(getConnection().getMetaData().getDatabaseProductName().toLowerCase());
		sql = dialect.getLimitString(sql, pageSize*(currPage-1), pageSize);
		
		String queryCount = "select count(1) as record_total from (${table}) t ";
		queryCount = StringUtil.replace(queryCount, "${table}", StringUtil.replace(qBase, "${orderby}", ""));
		
		Recordset rs = getDb().get(sql);
		rs.append("typename", java.sql.Types.VARCHAR);
		rs.top();
		while(rs.next()){
			String ToPatrol =rs.getString("topatrol");
			String ToFamily =rs.getString("tofamily");
			String ToService=rs.getString("toservice") ;
			String ToVolunteer=rs.getString("tovolunteer");
			StringBuffer sBuffer=new StringBuffer();
			ArrayList al=new ArrayList();
			if(!"".equals(ToPatrol)&&null!=ToPatrol){
				al.add(ToPatrol);
			}
			if(!"".equals(ToFamily)&&null!=ToFamily){
				al.add(ToFamily);
			}
			if(!"".equals(ToService)&&null!=ToService){
				al.add(ToService);
			}
			if(!"".equals(ToVolunteer)&&null!=ToVolunteer){
				al.add(ToVolunteer);
			}
			StringBuffer ss=new StringBuffer();
			for (int i = 0; i < al.size(); i++) {
				if(ss.length()==0){
					ss.append(al.get(i));
				}else{
					ss.append(",").append(al.get(i));
				}
			}
			rs.setValue("typename", ss);
			
			
		}
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

