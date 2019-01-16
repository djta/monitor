package com.ccms.core.engine;

import dinamica.Db;
import dinamica.GenericTransaction;
import dinamica.Recordset;

public class UpdateAllSkill extends GenericTransaction {

	public int service(Recordset inputs) throws Throwable{
		int rc = super.service(inputs);
		
		String dependent_col = getConfig().getConfigValue("dependent-col");
		String colname = getConfig().getConfigValue("colname");
		String template = getConfig().getConfigValue("sql-template");
		
		if(dependent_col != null && dependent_col.length() > 0 && colname != null 
				&& colname.length() > 0 && template != null && template.length() > 0){
			Db db = getDb();
			String[] dcs = dependent_col.split(";");
			String[] cols = colname.split(";");
			String[] sqls = template.split(";");
			boolean flag = false;//标志是否有需要更新的数据
			for(int i=0;i<dcs.length;i++){
				String[] changeValue = getRequest().getParameterValues(dcs[i]);
				String[] req_cols = cols[i].split(",");
				String sql = getResource(sqls[i]);
				if(changeValue != null && changeValue.length > 0){
					for(int j=0;j<changeValue.length;j++){
						if("1".equals(changeValue[j])){//当前行有变化
							Recordset detail = new Recordset();
							for (int m=0;m<req_cols.length;m++)
							{
					            detail.append(req_cols[m], java.sql.Types.VARCHAR);
							}
							detail.addNew();
							for (int m=0;m<req_cols.length;m++)
							{
								String v = getRequest().getParameterValues(req_cols[m])[j];
								detail.setValue(req_cols[m], (v!=null&&v.length()==0)?null:v);
							}
							db.addBatchCommand(getSQL(sql,detail));
							flag = true;
						}
					}
				}
			}
			if(flag == true){
				db.exec();
			}
		}
		return rc;
	}
}
