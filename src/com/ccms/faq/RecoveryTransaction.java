package com.ccms.faq;

import com.ccms.util.lunece.recovery.RecoveryError;
import dinamica.GenericTransaction;
import dinamica.Recordset;

/**
 * 建议
 * 搜索相关关键字
 * @author hdh
 *
 */
public class RecoveryTransaction extends GenericTransaction {

	public int service(Recordset inputParams) throws Throwable {
		
		int rc = super.service(inputParams);		
		
		Recordset data = null;
		
		String search = inputParams.getString("show_name");
		
		String path = getContext().getInitParameter("index-path");
		
		String dictionary = getContext().getRealPath("/")+this.getConfig().path+"dictionary.txt";
		
		data = RecoveryError.iuRecovery(path+"/recovery", dictionary, search);		
		
		/*if (data.getRecordCount()>0)
		{*/				  
			publish("query-revocery.sql", data);
		//} 		 
		
		return rc ;
	}
}
