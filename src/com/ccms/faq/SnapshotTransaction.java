package com.ccms.faq;
 
import com.ccms.util.lunece.core.IndexManage;
import dinamica.GenericTransaction;
import dinamica.Recordset;

/**
 *  索引明细
 * @author hdh
 *
 */
public class SnapshotTransaction extends GenericTransaction {
	
   
	public int service(Recordset inputParams) throws Throwable {
	
		int rc = super.service(inputParams);		
		
		Recordset data = null;
		
		String value = inputParams.getString("show_name");
		
		int tuid = inputParams.getInt("faq_id");
		
		if(value != null && !"".equals(value) && tuid>-1 )
		{
			String path = getContext().getInitParameter("index-path")+"/faq/"+getSession().getAttribute("dinamica.user.subject");
			
			String[] columns = {"show_name","lable","content"};
			
			data = IndexManage.queryIndexDetails(path, columns, "tuid",String.valueOf(tuid),value);
			
			if (data.getRecordCount()>0)
			{				  
				publish("query_snapshot.sql", data);
			}
		}
		 return rc ;
  	}
}
