package com.ccms.faq;

import com.ccms.util.lunece.core.IndexManage;
import dinamica.GenericTransaction;
import dinamica.Recordset;

/**
 * 回收箱管理
 * @author hdh
 *
 */
public class RecycleTransaction extends GenericTransaction {

	public int service(Recordset inputParams) throws Throwable {
		
		int rc = super.service(inputParams);	
		
		String action = inputParams.getString("action");		
		
		/**
		 *  is delete 
		 *  update index.
		 */
		if("delete".equals(action)){
			String sql = getSQL(getResource("update_status.sql"),inputParams);	
			super.getDb().exec(sql);			
			String path = getContext().getInitParameter("index-path")+"/faq/"+getSession().getAttribute("dinamica.user.subject");
			IndexManage.deleteIndex(path, "tuid", inputParams.getString("tuid"));			
			
		/**
		 *  is reduction
		 *  update index.	
		 */
		}else if("reduction".equals(action)){
			String sql = getSQL(getResource("query.sql"),inputParams);	
			Recordset data = super.getDb().get(sql);			
			String path = getContext().getInitParameter("index-path")+"/faq/"+getSession().getAttribute("dinamica.user.subject");
			IndexManage.addIndex(path, data);
			sql =  getSQL(getResource("update_status.sql"),inputParams);	
			super.getDb().exec(sql);			
			
		// is remove
		}else if("remove".equals(action)){
			String sql = getSQL(getResource("delete.sql"),inputParams);	
			this.getDb().exec(sql);
			sql = getSQL(getResource("delete_master_detail.sql"),inputParams);	
			this.getDb().exec(sql);
			
		// is removeAll
		}else if("removeAll".equals(action)){
			String sql = getResource("remove_all.sql");
			super.getDb().exec(sql);
			sql = getResource("remove_master_detail.sql");
			super.getDb().exec(sql);				
			
		}
			
		return rc ;
		
	}

}
