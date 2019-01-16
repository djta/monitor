package com.ccms.faq;

import com.ccms.util.lunece.core.IndexManage;
import dinamica.GenericTransaction;
import dinamica.Recordset;

public class RebuildFaqIndex extends GenericTransaction {
	public int service(Recordset inputParams) throws Throwable {
		int rc = super.service(inputParams);
		System.out.println(getSession().getAttribute("dinamica.user.subject"));
		String path = getContext().getInitParameter("index-path")+"/faq/"+getSession().getAttribute("dinamica.user.subject");
		
		String[] columns = {"show_name","lable","content"};
		
		String sql = getSQL(getResource("query.sql"),inputParams);
		Recordset rs = getDb().get(sql);
		
		IndexManage.rebuildIndex(path, columns, "tuid", rs);
		
		return rc;
	}
}
