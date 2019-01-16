package com.ccms.faq;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Types;

import com.ccms.dialect.Dialect;
import com.ccms.dialect.DialectFactory;
import com.ccms.util.lunece.core.IndexManage;

import dinamica.Config;
import dinamica.GenericTransaction;
import dinamica.Recordset;
import dinamica.StringUtil;
import dinamica.xml.Document;
import dinamica.xml.Element;

public class SearchFaqByIndex extends GenericTransaction {

	public int service(Recordset inputs) throws Throwable
	{
		int rc = super.service(inputs);

		String sqlFile = getConfig().getConfigValue("sql-template","query-base.sql");        //get base sql template
		String pagingRecordsetName = getConfig().getConfigValue("paging-recordset","query.sql");    //get paging recordset name
		String filterClauseName = getConfig().getConfigValue("filter-clause","filter.clause");    //get filter clause 
		String pageSizeStr = getConfig().getConfigValue("paging-pagesize","10");
		String totalName = getConfig().getConfigValue("total-recordset","query-page.sql");
		
		//分页排序相关
		String page = getRequest().getParameter("pageNo");
		String sort = getRequest().getParameter("sort");
		String order = getRequest().getParameter("order");
		
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
			}catch(Throwable a){
				
			}
		}
		if(sort != null && sort.length() > 0){
			orderby = " order by " + sort;
			if(order != null && order.length() > 0){
				orderby += " " + order;
			}
		}
		
		Recordset rs = null;
		String value = inputs.getString("show_name");
		if(value != null && !"".equals(value)){
			String path = getContext().getInitParameter("index-path")+"/faq/"+getSession().getAttribute("dinamica.user.subject");
			
			File file = new File(path);
			if(!file.exists() && !file.isDirectory()){
				if(file.mkdirs()){
					System.out.println("创建目录："+path);
				}else{
					System.out.println("创建目录失败");
				}
			}
			
			String[] columns = {"show_name","lable","content"};
			rs = IndexManage.queryIndex(path, columns, "tuid", value);
			super.getDb().exec(getSQL(super.getResource("insert_search_word.sql"),inputs));
		}
		
		String qBase = getResource(sqlFile);
		StringBuffer qFilter = new StringBuffer();
		Config c = getConfig();
		Document doc = c.getDocument();
		Element elColName[] = doc.getElements("colname");
		Element elOperatorName[] = doc.getElements("operator");
		if (elColName!=null)
		{
			for (int i = 0; i < elColName.length; i++) 
			{
				Element e = elColName[i];
				String colName = e.getString();
        		if(colName != null) {
            		String[] params = colName.split(",");
            
            		for (int j=0;j<params.length;j++)
            		{
            			if (inputs.getValue(params[j])!=null)
            				qFilter.append(getResource("clause-" + params[j]+ ".sql"));
            		}
            	}
			}
		}
		String where = qFilter.toString();
		if (elOperatorName!=null)
		{
			for (int i = 0; i < elOperatorName.length; i++) 
			{
				Element e = elOperatorName[i];
				String operatorName = e.getString();
                if (operatorName != null) {
            		String[] operators = operatorName.split(",");
            		for (int j=0;j<operators.length;j++)
            		{
            			if (inputs.getValue(operators[j])!=null)
                    		where = StringUtil.replace(where,"${"+operators[j]+"}", inputs.getString(operators[j]));
            		}
            	}
			}
		}
		getSession().setAttribute(filterClauseName, getSQL(where, inputs));
		qBase = StringUtil.replace(qBase,"${filter}", where);
		qBase = getSQL(qBase, inputs);
		
		if(rs == null || rs.getRecordCount() == 0){//如果根据索引查不出来则直接查数据库
			String sql = StringUtil.replace(qBase, "${orderby}", orderby);
			Dialect dialect = DialectFactory.buildDialect(getConnection().getMetaData().getDatabaseProductName().toLowerCase());
			sql = dialect.getLimitString(sql, pageSize*(currPage-1), pageSize);
			rs = getDb().get(sql);
		}
		
		String queryCount = "select count(1) as record_total from (${table}) t ";
		queryCount = StringUtil.replace(queryCount, "${table}", StringUtil.replace(qBase, "${orderby}", ""));
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
		
		publish(totalName, rsPage);
		publish(pagingRecordsetName, rs);
		
		return rc;
	}
}
