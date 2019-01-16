package transactions.project.weixin.store.club;

import dinamica.GenericOutput;
import dinamica.GenericTransaction;
import dinamica.Recordset;
import dinamica.StringUtil;
import dinamica.TemplateEngine;

/**
 *	输出问卷统计
 */
public class HGridOutpitForTerm extends GenericOutput{
	public void print(TemplateEngine te, GenericTransaction data)
	    throws Throwable
	{
		String rsName = getConfig().getConfigValue("hgrid-recordset");
		Recordset rs = (Recordset)getSession().getAttribute(rsName);
		String tRow  = getResource(getConfig().getConfigValue("row-template"));
		String tCol  = getResource(getConfig().getConfigValue("col-template"));
		String tRow_head  = getResource(getConfig().getConfigValue("row-head-template"));
		String tCol_head  = getResource(getConfig().getConfigValue("col-head-template"));
		
		StringBuffer hgrid = new StringBuffer();
		rs.top();
		while(rs.next()){
			StringBuilder colsBuf = new StringBuilder();
		    for (int i=0;i<rs.getFieldCount();i++)
		    {
		    	String value = rs.getString("data"+i);
		    	if(rs.getRecordNumber()==0){
		    		colsBuf.append(StringUtil.replace(tCol_head, "${value}", value));
				}else{
					colsBuf.append(StringUtil.replace(tCol, "${value}", value));
				}
		    }
		    if(rs.getRecordNumber()==0){
		    	hgrid.append(StringUtil.replace(tRow_head,"${cols}", colsBuf.toString()));
		    }else{
		    	hgrid.append(StringUtil.replace(tRow,"${cols}", colsBuf.toString()));
		    }
		}
		
		te.replace("${hgrid}", hgrid.toString());
		
		super.print(te, data);        
	}
}
