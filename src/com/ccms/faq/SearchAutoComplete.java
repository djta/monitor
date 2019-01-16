package com.ccms.faq;


import javax.servlet.http.HttpServletResponse;

import com.ccms.dialect.Dialect;
import com.ccms.dialect.DialectFactory;

import dinamica.GenericTransaction;
import dinamica.Recordset;

public class SearchAutoComplete extends GenericTransaction {
	
	public int service(Recordset inputParams) throws Throwable {
		
		int rc = super.service(inputParams);		

		String query = getConfig().getConfigValue("query","search.sql");
		String queryLimit = getConfig().getConfigValue("query-limit","20");
		
		String search = inputParams.getString("q");
		inputParams.setValue("q", search = search!=null?search.trim():"");
		String sql  = getSQL(getResource(query),inputParams);	
		
		Dialect dialect = DialectFactory.buildDialect(getConnection().getMetaData().getDatabaseProductName().toLowerCase());
		sql = dialect.getLimitString(sql, 0, Integer.parseInt(queryLimit));
		
		Recordset data = this.getDb().get(sql);
		data.top();
		StringBuilder json = new StringBuilder("{\"result\":[");
		if(data.next()){
			int i = 0 ;
			do{
				if(i==1){json.append(",");} else i=1;
				json.append("{\"id\":\"");
				json.append(data.getString("weight"));
				json.append("\",");
				json.append("\"name\":\"");
				json.append(data.getString("search_key"));
				json.append("\"}");		
			}while(data.next());
		}		
		json.append("]}");
		
		HttpServletResponse response = super.getResponse();
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().print(json.toString());	
		
		return rc;
	}

}
