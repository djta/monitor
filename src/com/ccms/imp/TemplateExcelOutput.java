package com.ccms.imp;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.sql.DataSource;

import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.util.CellRangeAddressList;

import dinamica.GenericOutput;
import dinamica.GenericTransaction;
import dinamica.Jndi;
import dinamica.Record;
import dinamica.Recordset;

public class TemplateExcelOutput extends GenericOutput {
	
	static final int rowMaxSize = 65535;
	
	private Map<String,String[]> city = new HashMap<String,String[]>();
	
	public void print(GenericTransaction data) throws Throwable
	{
		
		//store document into memory
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		createWorkbook(data, buf);
		
		//send output to browser
		byte[] b = buf.toByteArray();
		getResponse().setBufferSize(b.length);
		getResponse().setContentType("application/vnd.ms-excel");
		getResponse().setHeader("Content-Disposition", getAttachmentString());
		getResponse().setContentLength(b.length);
		ServletOutputStream out = getResponse().getOutputStream();
		out.write(b);
		
	}
	
	private void initDataBase()throws Throwable {
		String jndiPrefix = getContext().getInitParameter("jndi-prefix");
		String dataSourceName = getContext().getInitParameter("def-datasource");				
		if (getConfig().transDataSource!=null)
			dataSourceName = getConfig().transDataSource;		
		if (jndiPrefix==null)
			jndiPrefix="";		
		DataSource ds = Jndi.getDataSource(jndiPrefix + dataSourceName);		
		Connection conn = null;
		try
		{
			if(this.getConnection()==null){
				conn = ds.getConnection();
				this.setConnection(conn);
			}
		}
		catch (Throwable e){
			throw e;
		}
	}
	
	// 加载下拉列表项
	protected Map<String,String[]> initBoxList(List<Record> records)throws Throwable {		
		
		initDataBase();
		String sql  =  this.getResource("query_domain.sql");
		Record re = null ;
		Map<String,String[]> data = new HashMap<String,String[]>();
	
	    for (int i = 0; i < records.size(); i++) {
	    	re = records.get(i);
	    	String namespace = (String)re.getFieldValue("domain_namespace");
	    	if("Province".equals(namespace)||"City".equals(namespace)){
	    		data.put(namespace, null);
	    		continue ;
	    	}
	    	if(namespace!=null && namespace.trim().length()>0 ){
	    	   nativeRecord(namespace,sql,data);		
	    	}
		}
	    
	     // 只对省份  市级 做联动
	    if(data.containsKey("Province") && data.containsKey("City")){
	   
	    	String citySQL  =  this.getResource("query-all_city.sql");
	    	Recordset record = this.getDb().get(citySQL); 	
	    	List<String> citys = 	null;    	
	    	record.top();	    	
	    	String prov  = null ,temp = null ,value = null;	    	
	    	if(record.next()){	    		
		    	do{		    	
		    		citys = new ArrayList<String>();
		    		if(temp!=null){
		    			citys.add(temp);
		    			citys.add(value);
		    			temp = value = null ;
		    		}else{
		    			prov = record.getString("province");
		    			citys.add(prov);
		    			citys.add(record.getString("city"));
		    		}
	
					boolean ret = false ; 
					while(ret = record.next()){
						temp = record.getString("province");
						value = record.getString("city");	
						if(prov.equals(temp)){ 
							citys.add(value);	
						}else{	
							city.put(prov, citys.toArray(new String[0]));
							prov = temp ;
							break ;		//非同一组跳出
						}
					}				
					if(!ret) {
						city.put(prov, citys.toArray(new String[0]));
						break ; //结束
					}					
					
		    	}while(temp!=null );
	    	}
	    	
	    }else if(data.containsKey("Province")) {
	    	nativeRecord("Province",sql,data);
	    	
	    }else if(data.containsKey("City")){
	    	nativeRecord("City",sql,data);			   
	    } 
	    
	  return data ;		
	}
	
	private void nativeRecord(String domain,String sql ,Map<String,String[]> data) throws Throwable{ 
		 Recordset record = this.getDb().get(String.format(sql, domain));
    	 String [] values = new String[record.getRecordCount()];
    	 record.top();
    	 int  n = 0 ;
		 while(record.next()){
			 values[n++] = record.getString("domain_text_cn");
		 }
		 data.put(domain, values);
	}
	
	//去重
    protected List<Record> clearRepeat(List<Record> records) throws Throwable{
    	
    	Map<String,Record> unique = new HashMap<String,Record>();    	
 	   
 	    Record re = null ;
 	    String colName = null ; 
 	   
 	    for (int i = 0; i < records.size(); i++) 
 	    {
 		  re = records.get(i);
 		  colName =   getValue(re.getFieldValue("col_name")).toUpperCase();
 		 
 		  if(unique.containsKey(colName) && getValue(re.getFieldValue("domain_namespace")).length()>0)
 		  {
 			  Record te  = unique.get(colName);
 			  
 			  if(getValue(te.getFieldValue("domain_namespace")).length()==0){
 				 unique.put(colName, re);
 			  }
 			  
 		  }
 		  else if(!unique.containsKey(colName) && colName.length()>0)
 		  {
 			  unique.put(colName, re);
 		  }
 		  
 		}
 	    
 	    List<Record> tempRe = new ArrayList<Record>();
 	    Iterator<Record>  itR  = unique.values().iterator();
 	    while(itR.hasNext()) {
 	    	tempRe.add(itR.next()); 	
 	    }

		return tempRe ;
	}	
	    
	public void createWorkbook(GenericTransaction data, ByteArrayOutputStream buf) throws Throwable
	{
		
		Recordset rsFields = data.getRecordset("query-import_field.sql");
		rsFields.first();
	    List<Record> records = rsFields.getData();

	    //去除重复的列
	    records = clearRepeat(records);
	
	    //排序
	    records = sortArray(records);  
	    
	    Recordset reportCss = data.getRecordset("query_css.sql");
	    reportCss.first();
		String fontName = reportCss.getString("font_name");
		Integer fontSize = reportCss.getInteger("font_size");				
		
		HSSFWorkbook hssfwb = new HSSFWorkbook();
		
		Map<String,String[]> boxList = initBoxList(records);
		
		HSSFCellStyle titleStyle = ExcelUtil.getExcelTitle(hssfwb,fontName,fontSize);
		HSSFCellStyle dataStyle = ExcelUtil.getExcelData(hssfwb,fontName,fontSize);
		
		//数据模板 
		HSSFSheet sheet = hssfwb.createSheet();			
		hssfwb.setSheetName(0, "data_template");	
		//sheet.setDefaultColumnWidth(3500); 
		
		//数据格式说明
		HSSFSheet descSheet = hssfwb.createSheet();
		hssfwb.setSheetName(1, "field_description");
		
		DataValidation data_validation = null ;
		
		HSSFRow row = sheet.createRow(0); 			
		HSSFCell cell = null ;
		String namespace = null ;
		int cityIndex = 0 , provIndex = 0 ;
		
		for (int i = 0; i < records.size(); i++) {
			cell = row.createCell(i);
			Object cn = records.get(i).getFieldValue("col_name");			
			cell.setCellValue(cn.toString());	
			cell.setCellStyle(titleStyle);			
			sheet.setColumnWidth(i,3500); 
			//下拉列表
			if(boxList.containsKey(namespace = (String)records.get(i).getFieldValue("domain_namespace"))){
								
				if(city.size()>0 && boxList.containsKey("Province") && boxList.containsKey("City")){
					
					if(namespace.equals("Province") || namespace.equals("City")){
						if(namespace.equals("Province")) provIndex = i ;					
						if(namespace.equals("City") ) cityIndex = i ;						
						continue ;
					}			
				}
				
				if(boxList.get(namespace)==null||boxList.get(namespace).length==0){
					continue ;
				}
				
				data_validation = ExcelUtil.getDataValidationList(boxList.get(namespace),1,rowMaxSize,i,i);
				String[] s  = boxList.get(namespace);
				StringBuilder str =  new StringBuilder(225);
				for (int j = 0; j < s.length; j++) {
					str.append(s[j]);
					if(str.length()>255)
						break ;
				}
				if(str.length()>255) {
					continue ;
				}
				try{
					sheet.addValidationData(data_validation); 
				}catch(Exception e){e.printStackTrace();}
				 
			}else {
				String content = getFieldTypeInfo(getValue(records.get(i).getFieldValue("field_type")),
						getValue(records.get(i).getFieldValue("field_length")),
								getValue(records.get(i).getFieldValue("is_mandatory")));
				data_validation = ExcelUtil.setCellPrompt(String.valueOf(cn), "格式："+content, 1, rowMaxSize, i, i);		
				sheet.addValidationData(data_validation);
			}
			
		}		

		// 城市
		if(city.size()>0 && boxList.containsKey("Province") && boxList.containsKey("City")){
	
			creatHideSheet(hssfwb,"hideselectinfosheet");  
			data_validation = ExcelUtil.getDataValidationByFormula("provinceInfo",1,rowMaxSize,provIndex,provIndex);  
		    sheet.addValidationData(data_validation);  

		    data_validation = ExcelUtil.getDataValidationByFormula("INDIRECT($"+ExcelUtil.getExcelColumnLabel(provIndex)+"2)",1,rowMaxSize,cityIndex,cityIndex);  
		    sheet.addValidationData(data_validation);  
		}
		      		
    		
		createDescWork(descSheet, records, titleStyle, dataStyle);
		
		hssfwb.write(buf);		
	}
	
	protected static String getValue(Object obj){
		 return obj!=null? obj.toString().trim() :"" ;
	}
	
	protected void creatHideSheet(HSSFWorkbook workbook,String hideSheetName)throws Throwable{  
		HSSFSheet hideselectinfosheet = workbook.createSheet(hideSheetName);
        HSSFRow zjProvinceRow = null ;
        Set<String> keys = city.keySet();
        if(keys.size()==0){        	
        	return ;
        }
        Iterator<String> it = keys.iterator();
        String key = null ;
        int i = 0 ;
        while(it.hasNext()){
        	zjProvinceRow = hideselectinfosheet.createRow(i++);  
        	ExcelUtil.createRow(zjProvinceRow, city.get(key = it.next())); 
        }
       
        workbook.setSheetHidden(workbook.getSheetIndex(hideSheetName), true);   //隐藏数据
        
        Name name;  
        name = workbook.createName();  
        name.setNameName("provinceInfo");  
        //name.setRefersToFormula("hideselectinfosheet!$A$1:$E$1");
        name.setRefersToFormula("hideselectinfosheet!$A$1:$A$"+keys.size()); 
        it = keys.iterator();
        i = 1 ;
        int rowNumber = 0 ;
        while(it.hasNext()){
        	 name = workbook.createName();  
             name.setNameName(key = it.next());               
             rowNumber = city.get(key).length ;
             name.setRefersToFormula("hideselectinfosheet!$B$"+(i)+":$"+ExcelUtil.getExcelColumnLabel(rowNumber-1)+"$"+i);  
             ++i ;
        }                       
	}
	
	protected static String getFieldTypeInfo(String fieldType,String len,String is_mandatory){
	
		if("varchar".equals(fieldType.toLowerCase())){
			fieldType = "字符串 类型,长度 "+len;
			
		}else if("date".equals(fieldType.toLowerCase())||"timestamp".equals(fieldType.toLowerCase())){
			fieldType = "日期类型";
			
		}else if("int4".equals(fieldType.toLowerCase())|| "integer".equals(fieldType.toLowerCase())){
			fieldType = "数字类型 ,长度 "+len;			
		}			
		
		return fieldType+("1".equals(is_mandatory)?",必填项.":".");		
		
	}	
	
	protected String getAttachmentString() {
		String fileName = "report";
		try {
			if(getRequest().getHeader("user-agent").indexOf("MSIE") != -1) {   
				fileName = java.net.URLEncoder.encode("template","utf-8"); 
				fileName = fileName.replaceAll("\\+", "%20"); //处理空格
			} else {   
				fileName = new String("template".getBytes("utf-8"),"iso-8859-1");   
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "attachment; filename=\"" + fileName + ".xls\";";
	}
	

	
	// 排序 show_order 列
	protected List<Record> sortArray(List<Record> records) throws Throwable{		
		
		List<Record> tempRe = records ;
	    Record re1 = null,re2 = null ;
	    int num1 = 0 , num2 = 0 ;
	    for(int n=1;n<tempRe.size();n++)  
        {  
            for(int m=n;m>0;m--)
            {
            	re1 = tempRe.get(m-1);
            	re2 = tempRe.get(m);    
       		  	Object order = re1.getFieldValue("show_order");
       		  	if(order==null || order.toString().trim().length()==0){
       		  		num1 = 1000;
       		  	}else {
       		  		try { num1 = Integer.valueOf(order.toString());
       		  		} catch (Exception e) {
       		  			num1 = 1000;
       		  		}
       		  	}
       		  	order = re2.getFieldValue("show_order");
       		  	if(order==null || order.toString().trim().length()==0){
       		  		num2 = 1000;
       		  	}else {
       		  		try { num2 = Integer.valueOf(order.toString());
       		  		} catch (Exception e) {
       		  			num2 = 1000;
       		  		}
       		  	}       		 
       		  	if(num1 > num2){
       		  		tempRe.set(m-1, re2);
       		  		tempRe.set(m, re1);
       		  	}            	 
            }
        }
	    return tempRe ;
	}
	
	protected static class ExcelUtil{
		
		/** 
		 * 
		 * @param report
		 *            报表对象
		 * @throws Exception
		 */
		protected static HSSFCellStyle getExcelTitle(HSSFWorkbook workbook ,String fontName,Integer fontSize)throws Throwable {

			HSSFFont fontBold = workbook.createFont();
			fontBold.setFontHeightInPoints((short) fontSize.shortValue());
			fontBold.setFontName(fontName);
			fontBold.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);	
			HSSFCellStyle style = workbook.createCellStyle();
			//style.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index);
			//style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			style.setFont(fontBold);
			setStyleBorder(style, true);				
				
			return style ;
		}
		
		protected static HSSFCellStyle getExcelData (HSSFWorkbook workbook ,String fontName,Integer fontSize) throws Throwable {
				
			// *****************定义字体*****************
			// 普通字体
			HSSFFont fontNormal = workbook.createFont();
			fontNormal.setFontHeightInPoints((short) fontSize.shortValue());
			fontNormal.setFontName(fontName);
			
			HSSFCellStyle style = workbook.createCellStyle();
			//data
			style = workbook.createCellStyle();
			style.setFont(fontNormal);
			setStyleBorder(style, true);
			return style ;		
		}		
		
		protected static String getExcelColumnLabel(int num){
		    String temp="";
		    double i=Math.floor(Math.log(25.0*(num)/26.0+1)/Math.log(26))+1;
		    if(i>1){
		     double sub=num-26*(Math.pow(26, i-1)-1)/25;
		     for(double j=i;j>0;j--){
		      temp= temp+(char)(sub/Math.pow(26, j-1)+65);
		      sub=sub%Math.pow(26, j-1);
		     }
		    }else{
		     temp= temp+(char)(num+65);
		    }
		    return temp;
		 }
		
		 // 写入一行数据
		protected static void createRow(HSSFRow currentRow,String[] textList){  
	        if(textList!=null&&textList.length>0){  
	            int i = 0;  
	            for(String cellValue : textList){  
	                HSSFCell userNameLableCell = currentRow.createCell(i++);  
	                userNameLableCell.setCellValue(cellValue);  
	            }  
	        }  
	    }  
		
		//提示 0，0
		protected static DataValidation setCellPrompt(String title,String content, int rowBegin,int rowEnd,int columnBegin,int columnEnd) {  
	        
	        DVConstraint constraint = DVConstraint.createCustomFormulaConstraint("BB2");  
	        
	        CellRangeAddressList regions = new CellRangeAddressList(rowBegin,rowEnd,columnBegin,columnEnd);  
	        
	        DataValidation data_validation_view = new HSSFDataValidation(regions,constraint);  
	        
	        data_validation_view.createPromptBox(title, content);    
	        
	        return data_validation_view;  
	    } 
		
		//列表   0,0 
		protected static DataValidation getDataValidationList(String[] selectTextList,int rowBegin,int rowEnd,int columnBegin,int columnEnd){  
	        
	        DVConstraint constraint = DVConstraint.createExplicitListConstraint(selectTextList);      
	       
	        CellRangeAddressList regions=new CellRangeAddressList(rowBegin,rowEnd,columnBegin,columnEnd);    
	        
	        DataValidation data_validation_list = new HSSFDataValidation(regions,constraint);    
	        
	        return data_validation_list;    
	    } 
		

	    // 联动下拉 0,0
		protected static DataValidation getDataValidationByFormula(String formulaString,int rowBegin,int rowEnd,int columnBegin,int columnEnd){  
	        
	        DVConstraint constraint = DVConstraint.createFormulaListConstraint(formulaString);   
	       
	        CellRangeAddressList regions=new CellRangeAddressList(rowBegin,rowEnd,columnBegin,columnEnd);    
	       
	        DataValidation data_validation_list = new HSSFDataValidation(regions,constraint);  
	        
	        return data_validation_list;    
	    }  
		
		protected static void setStyleBorder(HSSFCellStyle style, boolean haveBorder) {
			if (haveBorder) {
				style.setBorderBottom((short) 1);
				style.setBorderLeft((short) 1);
				style.setBorderRight((short) 1);
				style.setBorderTop((short) 1);
			} else {
				style.setBorderBottom((short) 0);
				style.setBorderLeft((short) 0);
				style.setBorderRight((short) 0);
				style.setBorderTop((short) 0);
			}
		}
		
	}	

    // 备注
    protected void createDescWork(HSSFSheet descSheet , List<Record> records,HSSFCellStyle titleStyle,HSSFCellStyle dataStyle) throws Throwable
	{
	
		HSSFRow descRow = descSheet.createRow(0);	
		
		HSSFCell cell = null ;
		String []  titles  =  {"字段名称","数据类型","数据长度","是否为必填","默认值"} ; 
		for (int i = 0; i < titles.length; i++) {
			cell = descRow.createCell(i);
			cell.setCellValue(titles[i]);
			cell.setCellStyle(titleStyle);
			descSheet.setColumnWidth(i, 5000);
		}		
		
		for (int i = 0; i < records.size(); i++) {
			descRow = descSheet.createRow(i+1);	
			
			Object cn = records.get(i).getFieldValue("col_name");
			cell = descRow.createCell(0);
			cell.setCellValue((String)cn);	
			cell.setCellStyle(dataStyle);
			
			String field_type = (String)records.get(i).getFieldValue("field_type");
			cell = descRow.createCell(1);
			cell.setCellValue(field_type);
			cell.setCellStyle(dataStyle);
			
			String field_length = (String)records.get(i).getFieldValue("field_length");
			cell = descRow.createCell(2);
			cell.setCellValue(field_length);
			cell.setCellStyle(dataStyle);
			
			String is_mandatory = (String)records.get(i).getFieldValue("is_mandatory");			
			cell = descRow.createCell(3);
			cell.setCellValue("1".equals(is_mandatory.toString())?"是":"否");			
			cell.setCellStyle(dataStyle);	
			
			Object obj  =  records.get(i).getFieldValue("default_value");
			cell = descRow.createCell(4);
			cell.setCellValue(obj==null?"空":obj.toString());
			cell.setCellStyle(dataStyle);				
		}
	}
}
