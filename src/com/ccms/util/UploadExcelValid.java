package com.ccms.util;

import java.io.File;

import jxl.Cell;
import jxl.JXLException;
import jxl.Sheet;
import jxl.Workbook;
import dinamica.Config;
import dinamica.GenericTransaction;
import dinamica.Recordset;
import dinamica.StringUtil;


public class UploadExcelValid extends GenericTransaction  {

 
	@Override
	public int service(Recordset inputParams) throws Throwable {
		
		int rc = super.service(inputParams);
		
		if (inputParams.isNull("file.filename"))
			throw new Throwable("导入文件不能为空!");

		String file = inputParams.getString("file");
		String fileName = (String) inputParams.getValue("file.filename");
		fileName = fileName.substring(fileName.lastIndexOf(File.separator) + 1);
		fileName = formatRequestEncoding(fileName);
		inputParams.setValue("file.filename", fileName);		

		int name_row =inputParams.getInt("name_row");
		int mobile_row =inputParams.getInt("mobile_row");		
		String cust_name_sql =getSQL( getResource("query-cust_name.sql"),inputParams);
		String mobile_sql =getSQL( getResource("query-mobile.sql"),inputParams);
		 
		
		Workbook wb;
		
		try {
			wb = Workbook.getWorkbook(new File(file));
		} catch (JXLException e1) {
			throw new Throwable ("Excel格式不正确，请使用excel 2003 格式文件。",e1);
		}		
		 
		Sheet sheet = wb.getSheet(0);	
		
		int numOfRows = sheet.getRows();		
		int columnas = sheet.getColumns();		
		if(numOfRows == 0 || columnas == 0){
			throw new Throwable("无数据!");
		}
		if(name_row>columnas){
			throw new Throwable("选择的姓名所在列大于excel的总列数");
		}		
		if(mobile_row>columnas){
			throw new Throwable("选择的手机号码所在大于excel的总列数");
		}
		
		Recordset rsData = new Recordset();	
		rsData.append( "cust_name", java.sql.Types.VARCHAR);
		rsData.append( "mobile", java.sql.Types.VARCHAR);
		rsData.append( "repetition", java.sql.Types.VARCHAR);
		
		for(int i=1 ; i<numOfRows;i++){
						
			Cell col_name = sheet.getCell(name_row-1,i);
			String cust_name = col_name.getContents();
			boolean cust_name_flag=getValid(cust_name_sql,"cust_name",cust_name);			
			
			Cell col_mobile = sheet.getCell(mobile_row-1, i);
			String mobile = col_mobile.getContents();
			boolean mobile_flag=getValid(mobile_sql,"mobile",mobile);
			
			if(cust_name_flag||mobile_flag){
				rsData.addNew();
				rsData.setValue("cust_name", cust_name);
				rsData.setValue("mobile", mobile);
				StringBuffer repetition=new StringBuffer() ;
				if(cust_name_flag){
					repetition.append("此姓名在系统中重复, ");
				}
				if(mobile_flag){
					repetition.append(" 此手机号码在系统中重复 ");
				}				
				rsData.setValue("repetition", repetition.toString());
			}
			
			
		} 
		

	    getRequest().getSession().setAttribute("rsData", rsData);
		publish("rsData", rsData);
		 
		return rc;
	}
	
	
	
	protected boolean getValid(String sql,String str ,String va ) throws Throwable {
		boolean flag=false;
		sql=StringUtil.replace(sql, "${"+str+"}", va);
		Recordset rs = getDb().get(sql);
		rs.first();
		if(rs.getInt("total") >0){
			flag=true;
		}
		return flag;
		
	}
	
	protected String formatRequestEncoding(String str) throws Throwable {

		Config _config = getConfig();

		// global encoding?
		String encoding = getContext().getInitParameter("request-encoding");
		if (encoding != null && encoding.trim().equalsIgnoreCase(""))
			encoding = null;

		// load resource with appropiate encoding if defined
		if (_config.requestEncoding != null)
			return new String(str.getBytes("ISO8859-1"), _config.requestEncoding);
		else if (encoding != null)
			return new String(str.getBytes("ISO8859-1"), encoding);
		else
			return str;
	}
	

}
