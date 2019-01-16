package com.ccms.report2;


import dinamica.*;

import javax.sql.DataSource;
import java.sql.*;

/**
 * Class description.
 * <br><br>
 * (c) 2004 Martin Cordova<br>
 * This code is released under the LGPL license<br>
 * Dinamica Framework - http://www.martincordova.com
 * @author Martin Cordova (dinamica@martincordova.com)
 * */
public class ReportFilterOutput extends GenericOutput{

	private String strExistsColumn = null;
	/* (non-Javadoc)
	 * @see dinamica.GenericOutput#print(dinamica.TemplateEngine, dinamica.GenericTransaction)
	 */
	public void print(TemplateEngine te, GenericTransaction data) throws Throwable {

		//reuse superclass code
		super.print(te, data);

		//retrieve main recordset (org titles)
		//Recordset org = data.getRecordset("org");


		//get datasource object
		String jndiPrefix = getContext().getInitParameter("jndi-prefix");
		String dataSourceName = getContext().getInitParameter("def-datasource");
				
		/* PATCH 2005-03-10 read datasource name from config.xml if available */
		if (getConfig().transDataSource!=null){
			dataSourceName = getConfig().transDataSource;
		}
		
		if (jndiPrefix==null){
			jndiPrefix="";
		}
		
		DataSource ds = Jndi.getDataSource(jndiPrefix + dataSourceName);
		Connection conn = null;

		try{
			if(this.getConnection()==null){
				//connect to database
				conn = ds.getConnection();
				this.setConnection(conn);
			}
		}catch (Throwable e){
			throw e;
		}		
		
		//加载模版
		String tCheckbox = getResource(getConfig().getConfigValue("checkbox-template"));
		String tRadiobox = getResource(getConfig().getConfigValue("radio-template"));
		String tText = getResource(getConfig().getConfigValue("text-template"));
		String tCombo = getResource(getConfig().getConfigValue("combo-template"));
		String tDate = getResource(getConfig().getConfigValue("date-template"));
		String tDateTime = getResource(getConfig().getConfigValue("datetime-template"));
		String tHidden = getResource(getConfig().getConfigValue("hidden-template"));
		String tTextReadonly = getResource(getConfig().getConfigValue("textreadonly-template"));
		String tTextarea = getResource(getConfig().getConfigValue("textarea-template"));
		String tPickup = getResource(getConfig().getConfigValue("pickup-template"));
		String tPlugin = getResource(getConfig().getConfigValue("plugin-template"));

		String strDomain = getResource(getConfig().getConfigValue("domainsql-template"));
		String strFkField = getResource(getConfig().getConfigValue("fk-field-sql-template"));
		strExistsColumn = getResource(getConfig().getConfigValue("sql-template-exists-column"));
		String strFkData = getResource(getConfig().getConfigValue("fk-data-sql-template"));
		
		TemplateEngine tData = new TemplateEngine(getContext(),getRequest(), strFkData);
		strFkData = tData.toString();
		
		TemplateEngine tExistsColumn = new TemplateEngine(getContext(),getRequest(), strExistsColumn);
		strExistsColumn = tExistsColumn.toString();

	    String tRow  = getResource(getConfig().getConfigValue("row-template"));
	    String rsName = getConfig().getConfigValue("field-recordset");
	    String rsTableName = getConfig().getConfigValue("table-recordset");

		Recordset rs = data.getRecordset(rsName);
		Recordset rsTable = data.getRecordset(rsTableName);
		rsTable.first();
		
		//取每行列数
		int cols = 1;
		if(rs.getRecordCount() > 0){
			rs.first();
			if(rs.getString("col_num")==null){
				cols = 1;
			}else{
				cols = Integer.parseInt(rs.getString("col_num"));
			}
			if(cols==0){
				cols = 1;
			}
		}
			
		Integer iShowType = 0;
		
		//hgrid body
		StringBuffer hgrid = new StringBuffer();
		if(rs.getRecordCount() > 0){
			for (int k=0;k<rs.getRecordCount();k=k+cols){
				StringBuilder colsBuf = new StringBuilder();
				for (int i=0;i<cols;i++){
					if (k+i==rs.getRecordCount()) {
						for (int j=i;j<cols;j++) {
							colsBuf.append("<td></td>");
						}
						break;
					}
	          	
					rs.setRecordNumber(k+i);

					if(rs.getString("show_type") != null){
						iShowType = rs.getInteger("show_type");
					}else{
						iShowType = 0;
					}

					switch (iShowType.intValue()) {
						case 0:  
							//System.out.println("TEXT"); 
							colsBuf.append(getText(rs,tText));
							break;
						case 1:  
							//System.out.println("COMBO"); 
							colsBuf.append(getCombo(rs,tCombo,strDomain,strFkField,strFkData));
							break;
						case 2:  
							//System.out.println("checkbox"); 
							colsBuf.append(getCheckbox(rs,tCheckbox));
							break;
						case 3:  
							//System.out.println("radio"); 
							colsBuf.append(getRadio(rs,tRadiobox,strDomain,strFkField,strFkData));
							break;
						case 4:  
							//System.out.println("readonly"); 
							colsBuf.append(getReadonly(rs,tTextReadonly));
							break;
						case 5:  
							//System.out.println("date"); 
							colsBuf.append(getDate(rs,tDate));
							break;
						case 6:  
							//System.out.println("textarea"); 
							colsBuf.append(getTextarea(rs,tTextarea));
							break;
						case 7:  
							//System.out.println("textarea"); 
							colsBuf.append(getPickup(rs,tPickup));
							break;
						case 8:  
							//System.out.println("textarea"); 
							colsBuf.append(getPlugin(rs,getPlugin(rs,tPlugin)));
							break;
						case 9:  
							//System.out.println("hidden"); 
							colsBuf.append(getHidden(rs,tHidden));
							break;
						case 10:
							//System.out.println("dateTime"); 
							colsBuf.append(getDateTime(rs,tDateTime));
							break;
						default: 
							//System.out.println("未指定控件类型.");
							colsBuf.append(getText(rs,tText));
							break;
					}
				}
				hgrid.append(StringUtil.replace(tRow,"${cols}", colsBuf.toString()));
			}
		}
		//replace into main template
		te.replace(rsTable,"");
		te.replace("${controls}", hgrid.toString());

		try{
			//connect to database
			conn.close();
		}catch (Throwable e){
			throw e;
		}		
	}

	String getText (Recordset rs,String te) throws Throwable
	{
		TemplateEngine t = new TemplateEngine(getContext(),getRequest(), te);
		t.replace(rs,"");
		return t.toString();		
	}

	String getCombo (Recordset rs,String te,String domain,String strFkField,String strFkData) throws Throwable
	{

		Db db = getDb();
		//取域值
		TemplateEngine t1 = new TemplateEngine(getContext(),getRequest(), domain);
		t1.replace(rs,"");		
		Recordset rsDomain = db.get(t1.toString());
		
		TemplateEngine t = new TemplateEngine(getContext(),getRequest(), te);
		t.replace(rs,"");
		if(rsDomain.getRecordCount()>0){
			t.replace(rsDomain,"","rows");
		}else{
			TemplateEngine t2 = new TemplateEngine(getContext(),getRequest(), strFkField);
			t2.replace(rs,"");		
			Recordset rsFkFiled = db.get(t2.toString());
			if(rsFkFiled.next()){
				rsFkFiled.first();
				String schema = rsFkFiled.getString("schema_code");
				String table = rsFkFiled.getString("table_code");
				String field = rsFkFiled.getString("field_code");
				String field_alias = rsFkFiled.getString("field_alias");
				String fk_references = rsFkFiled.getString("fk_references");
				//外键配置数据不完整
				if(table==null || field==null || field_alias==null)
				{
					return t.toString();
				}
				strFkData = StringUtil.replace(strFkData, "${schema}", schema);
				strFkData = StringUtil.replace(strFkData, "${table}", table);
				strFkData = StringUtil.replace(strFkData, "${field_code}", field);
				strFkData = StringUtil.replace(strFkData, "${field_alias}", field_alias);
				strFkData = StringUtil.replace(strFkData, "${field_reference}", fk_references);

				String sqlExistsColumn = StringUtil.replace(strExistsColumn, "${table}", table);
				sqlExistsColumn = StringUtil.replace(sqlExistsColumn, "${field_code}", "subject_id");
				Recordset rsExistsField = db.get(sqlExistsColumn);
				rsExistsField.first();
				String flag = rsExistsField.getString("flag");
				if("1".equals(flag)){
					strFkData = StringUtil.replace(strFkData, "${result_field}", "subject_id");
					strFkData = StringUtil.replace(strFkData, "${result}", "1");
				}else{
					strFkData = StringUtil.replace(strFkData, "${result_field}", "0");
					strFkData = StringUtil.replace(strFkData, "${result}", "0");
				}
				
				Recordset rsFkData = db.get(strFkData);

				t.replace(rsFkData,"","rows");
			}
		}

		return t.toString();	
	}

	String getCheckbox (Recordset rs,String te) throws Throwable
	{
		TemplateEngine t = new TemplateEngine(getContext(),getRequest(), te);
		t.replace(rs,"");
		return t.toString();	
	}

	String getRadio (Recordset rs,String te,String domain,String strFkField,String strFkData) throws Throwable
	{
		Db db = getDb();
		//取域值
		TemplateEngine t1 = new TemplateEngine(getContext(),getRequest(), domain);
		t1.replace(rs,"");	
		Recordset rsDomain = db.get(t1.toString());
		
		TemplateEngine t = new TemplateEngine(getContext(),getRequest(), te);
		t.replace(rs,"");
		if(rsDomain.getRecordCount()>0){
			t.replace(rsDomain,"","rows");
		}else{
			TemplateEngine t2 = new TemplateEngine(getContext(),getRequest(), strFkField);
			t2.replace(rs,"");		
			Recordset rsFkFiled = db.get(t2.toString());
			if(rsFkFiled.next()){
				rsFkFiled.first();
				String schema = rsFkFiled.getString("schema_code");
				String table = rsFkFiled.getString("table_code");
				String field = rsFkFiled.getString("field_code");
				String field_alias = rsFkFiled.getString("field_alias");
				String fk_references = rsFkFiled.getString("fk_references");
				//外键配置数据不完整
				if(table==null || field==null || field_alias==null)
				{
					return t.toString();
				}
				strFkData = StringUtil.replace(strFkData, "${schema}", schema);
				strFkData = StringUtil.replace(strFkData, "${table}", table);
				strFkData = StringUtil.replace(strFkData, "${field_code}", field);
				strFkData = StringUtil.replace(strFkData, "${field_alias}", field_alias);
				strFkData = StringUtil.replace(strFkData, "${field_reference}", fk_references);

				String sqlExistsColumn = StringUtil.replace(strExistsColumn, "${table}", table);
				sqlExistsColumn = StringUtil.replace(sqlExistsColumn, "${field_code}", "subject_id");
				Recordset rsExistsField = db.get(sqlExistsColumn);
				rsExistsField.first();
				String flag = rsExistsField.getString("flag");
				if("1".equals(flag)){
					strFkData = StringUtil.replace(strFkData, "${result_field}", "subject_id");
					strFkData = StringUtil.replace(strFkData, "${result}", "1");
				}else{
					strFkData = StringUtil.replace(strFkData, "${result_field}", "0");
					strFkData = StringUtil.replace(strFkData, "${result}", "0");
				}
				
				Recordset rsFkData = db.get(strFkData);

				t.replace(rsFkData,"","rows");
			}
		}

		return t.toString();		
	}

	String getReadonly (Recordset rs,String te) throws Throwable
	{
		TemplateEngine t = new TemplateEngine(getContext(),getRequest(), te);
		t.replace(rs,"");
		return t.toString();		
	}

	String getDate (Recordset rs,String te) throws Throwable
	{
		TemplateEngine t = new TemplateEngine(getContext(),getRequest(), te);
		t.replace(rs,"");
		return t.toString();		
	}
	
	String getDateTime (Recordset rs,String te) throws Throwable
	{
		TemplateEngine t = new TemplateEngine(getContext(),getRequest(), te);
		t.replace(rs,"");
		return t.toString();		
	}

	String getTextarea (Recordset rs,String te) throws Throwable
	{
		TemplateEngine t = new TemplateEngine(getContext(),getRequest(), te);
		t.replace(rs,"");
		return t.toString();		
	}

	String getPickup (Recordset rs,String te) throws Throwable
	{
		TemplateEngine t = new TemplateEngine(getContext(),getRequest(), te);
		t.replace(rs,"");
		return t.toString();
	}

	String getPlugin (Recordset rs,String te) throws Throwable
	{
		TemplateEngine t = new TemplateEngine(getContext(),getRequest(), te);
		t.replace(rs,"");
		return t.toString();
	}

	String getHidden (Recordset rs,String te) throws Throwable
	{
		TemplateEngine t = new TemplateEngine(getContext(),getRequest(), te);
		t.replace(rs,"");
		return t.toString();		
	}

}
