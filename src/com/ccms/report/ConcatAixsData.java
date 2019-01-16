package com.ccms.report;

import dinamica.Db;
import dinamica.GenericTableManager;
import dinamica.Recordset;
import dinamica.StringUtil;
import dinamica.TemplateEngine;

/**
 * Insert master record and an associated array of values (detail) 
 * <br><br>
 * Creation date: 5/03/2004<br>
 * Last Update: 5/03/2004<br>
 * (c) 2004 Martin Cordova<br>
 * This code is released under the LGPL license<br>
 * @author Martin Cordova (dinamica@martincordova.com)
 * */
public class ConcatAixsData extends GenericTableManager
{

	/* (non-Javadoc)
	 * @see dinamica.GenericTransaction#service(dinamica.Recordset)
	 */
	public int service(Recordset inputParams) throws Throwable
	{

		//execute all recordsets and queries defined in config.xml
		int rc = 0;
		super.service(inputParams);
				
		//read confir parameters
		String colNames = getConfig().getConfigValue("colname");
		String sqlFiles = getConfig().getConfigValue("sql-template-update");
		String sqlRootFiles = getConfig().getConfigValue("sql-template");

		String is_save = inputParams.getString("is_save");
		if(is_save!=null&&is_save.equals("1")){
			if(colNames != null) {           //update by Oasahi
	    		String[] colNamesA = colNames.split(";");   //多个语句体用分号(;)分隔       "xxx1,yyy1;xxx2,yyy2"
	    		String[] sqlFilesA = sqlFiles.split(";");   //SQL语句
	            
	    		for (int i=0;i<colNamesA.length;i++)
	    		{
	                String sqlFile = sqlFilesA[i];

	        		//pre-fill sql template with master record values
	        		String sql = getSQL(getResource(sqlFile), inputParams);
	        		
	        		save(sql, colNamesA[i]);
	    		}
	    	}
			
		}

		//get db channel
		Db db = getDb();
		String[] FiledName = null;   //保存字段名
		String[][] FiledValue = null;   //保存字段值

		if(colNames != null) {           //update by Oasahi
    		String[] colNameA = colNames.split(",");   //单语句体中,多个字段,用逗号(,)分隔  "xxx1,yyy1"
    		if(getRequest().getParameterValues(colNameA[0])!=null){/*先判断值是否存在*/
    			int recordCount = getRequest().getParameterValues(colNameA[0]).length;
    			
    			FiledName = new String [colNameA.length];
    			FiledValue = new String [colNameA.length][recordCount];
    			
                for(int i=0;i<colNameA.length;i++){
        			for (int j=0;j<recordCount;j++){
            			FiledName[i] = colNameA[i];
            			String [] colNameAValues = getRequest().getParameterValues(colNameA[i]);
            			if(colNameAValues!=null && colNameAValues.length>j){
            				FiledValue[i][j] = colNameAValues[j];
            			}
        			}
                }
        	}
    	}
		
		//拼结果集
		Recordset rsReportFilter = getRecordset("report.filter.params");

		// 生成暂存变量值的结果集
		Recordset detail = new Recordset();
		detail.append("parameter", java.sql.Types.VARCHAR);
		detail.append("value", java.sql.Types.VARCHAR);
		detail.addNew();
		Recordset rsField = getRecordset("query_filter_field.sql");

		// 取参数变量值
		rsField.top();
		while (rsField.next()) {
			String field_alias = rsField.getString("field_code_alias");
			String value = "";
			String data[] = getRequest().getParameterValues(field_alias + "_filter");
			String data_operator[] = getRequest().getParameterValues(field_alias + "_operator");

			if (data != null && !data[0].trim().equals("")) {
				/* only accept single value parameters - not arrays */
				if (data.length == 1) {
					value = data[0].trim();
					if (data_operator[0].trim().equals("like")) {
						value = "%" + value + "%";
					}
					detail.setValue("parameter", field_alias);
					detail.setValue("value", value);
					detail.addNew();
				}
			} 
		}

		String sql = getSQL(getResource(sqlRootFiles), inputParams);

		sql=StringUtil.replace(sql, "${FLD", "${fld");
		TemplateEngine t = new TemplateEngine(null, null, sql);
		t.replace(detail, "", "filter-params");

		sql = getSQL(t.toString(), rsReportFilter);
		Recordset rs = db.get(sql);
		Recordset rsAixsX = rs.copyStructure();
		Recordset rsAixsY = rs.copyStructure();
		Recordset rsAixsZ = rs.copyStructure();
		
		rs.top();
		while(rs.next()){
			if( null == FiledName ){
				continue;
			}
			for(int i=0;i<FiledName.length;i++){
				for(int j=0;j<FiledValue[i].length;j++){
					String report_tuid=rs.getString("field_report");
					if(FiledName[i].equals("field_report")&&FiledValue[i][j].equals(report_tuid)){
						for(int k=0;k<FiledName.length;k++){
							if(FiledName[k].equals("report_order")){	//显示顺序
								rs.setValue("show_order", new Integer(FiledValue[k][j]==null?"0":FiledValue[k][j]));
							}
						}
						for(int k=0;k<FiledName.length;k++){
							if(FiledName[k].equals("is_row_head")&&"1".equals(FiledValue[k][j])){	//X轴
								rsAixsX.addNew();
								rs.copyValues(rsAixsX);
							}
							if(FiledName[k].equals("is_col_head")&&"1".equals(FiledValue[k][j])){	//Y轴
								rsAixsY.addNew();
								rs.copyValues(rsAixsY);
							}
						}
					}
				}
			}
			//交叉值从数据库取
			if("1".equals(rs.getString("is_cross_value"))){
				rsAixsZ.addNew();
				rs.copyValues(rsAixsZ);
			}
		}

		rsAixsX.sort("show_order");
		rsAixsY.sort("show_order");
		rsAixsZ.sort("show_order");
		getSession().setAttribute("query_rowhead_field.sql", rsAixsX);
		getSession().setAttribute("query_colhead_field.sql", rsAixsY);
		getSession().setAttribute("query_crossvalue_field.sql", rsAixsZ);
		publish("query_rowhead_field.sql", rsAixsX);
		publish("query_colhead_field.sql", rsAixsY);
		publish("query_crossvalue_field.sql", rsAixsZ);
		
		return rc;
		
	}

	public void save(String sql, String colNames) throws Throwable
	{
		
		Db db = getDb();
		String[] colNameA = colNames.split(",");   //单语句体中,多个字段,用逗号(,)分隔  "xxx1,yyy1"
		Recordset detail = new Recordset();
		for (int i=0;i<colNameA.length;i++)
		{
            detail.append(colNameA[i], java.sql.Types.VARCHAR);
		}

		if(getRequest().getParameterValues(colNameA[0])!=null){/*先判断值是否存在*/
            int recordCount = getRequest().getParameterValues(colNameA[0]).length;
            for(int i=0;i<recordCount;i++){
    			detail.addNew();
    			for (int j=0;j<colNameA.length;j++){
    			    String colValue[] = getRequest().getParameterValues(colNameA[j]);
            		if(colValue[i] != null && colValue[i].trim().length() > 0){
        				detail.setValue(colNameA[j], colValue[i].trim());
            		}else{
        				detail.setValue(colNameA[j], null);
            		}
    			}
    			String cmd = getSQL(sql, detail);
    			db.addBatchCommand(cmd);
            }
    		db.exec();
    	}
	}
}
