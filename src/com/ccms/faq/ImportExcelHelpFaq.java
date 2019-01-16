package com.ccms.faq;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jxl.Cell;
import jxl.JXLException;
import jxl.Sheet;
import jxl.Workbook;
import dinamica.Db;
import dinamica.GenericTableManager;
import dinamica.Recordset;
import dinamica.StringUtil;

public class ImportExcelHelpFaq extends GenericTableManager{

	String columna = null;
	
	public int service(Recordset inputParams) throws Throwable {
		int rc = super.service(inputParams);
		
		Recordset rsError = new Recordset();
				
		rsError.append("columna", java.sql.Types.VARCHAR);
		rsError.append("fila", java.sql.Types.INTEGER);
		rsError.append("error", java.sql.Types.VARCHAR);
				
		getSession().setAttribute("error.excel",rsError);
		
		inputParams.setValue("total_errores", new Integer(rsError.getRecordCount()));
		
		if (inputParams.isNull("file.filename"))
			throw new Throwable("文件名不能为空！");

		String file = inputParams.getString("file");
		Workbook wb;
		
		try {
			wb = Workbook.getWorkbook(new File(file));
		} catch (JXLException e1) {
			throw new Throwable ("文件格式无法识别，请重新选择！",e1);
		}
		
		Sheet sheet = wb.getSheet(0);
		
		int numOfRows = sheet.getRows();
		
		Recordset rs = new Recordset();
					 
		rs.append("show_name", java.sql.Types.VARCHAR);
		rs.append("lable", java.sql.Types.VARCHAR);
		rs.append("content", java.sql.Types.VARCHAR);

		//校验excel首行标签定义的合法性
		String[] columNames = {"show_name","lable","content"};
		List<String> list = new ArrayList<String>();
		for(int j=0;j<3;j++){
			Cell cell = sheet.getCell(j,0);
			String str = cell.getContents();
			if (str == null || str.equals(""))
			{
				throw new Throwable ("首行标签不能为空！");
			}
			else
			{
				boolean flag = false;
				for(int n=0;n<columNames.length;n++){
					if(str.equals(columNames[n])){
						flag = true;
						break;
					}
				}
				if(flag == true){
					boolean chong = true;
					for(int m=0;m<list.size();m++){
						if(str.equals(list.get(m))){
							chong = false;
							break;
						}
					}
					if(chong == false){
						throw new Throwable ("标签("+str+")定义重复！");
					}else{
						list.add(str);
					}
				}else{
					throw new Throwable ("标签("+str+")不存在！");
				}
			}
		}
		for(int i = 1; i<numOfRows;i++)
		{ 
			try
			{
				rs.addNew();
				for(int k=0;k<3;k++){
					Cell column = sheet.getCell(k,i);
					String content = column.getContents();
					columna = list.get(k);
					if (content == null || content.equals(""))
					{
						throw new Throwable ("该单元格不能为空！");
					}else{
						if("show_name".equals(list.get(k))){
							rs.setValue("show_name", content);			
						}else if("lable".equals(list.get(k))){
							rs.setValue("lable", content);
						}else if("content".equals(list.get(k))){
							rs.setValue("content", content);	
						}
					}
				}
			}
			catch (Throwable a)
			{
				rsError.addNew();
				rsError.setValue("columna", columna);
				rsError.setValue("fila", (i+1));
				rsError.setValue("error", a.getMessage());

				inputParams.setValue("total_errores", new Integer(rsError.getRecordCount()));

				if (rsError.getRecordCount()==20)
				{
					getSession().setAttribute("error.excel",rsError);
					throw new Throwable("Excel文件包含错误已经超过 20 处！");
				}
			}
		}
		
		if (rsError.getRecordCount()>0) 
		{
			getSession().setAttribute("error.excel",rsError);
			throw new Throwable("Excel文件包含错误！");
		}
						
		inputParams.setValue("total_registros", new Integer(rs.getRecordCount()));
		
		Db db = getDb();
		String sql = getSQL(getResource("insert.sql"), inputParams);
		rs.top();
		while(rs.next()){
			String show_name = rs.getString("show_name");
			String lable = rs.getString("lable");
			String content = rs.getString("content");
			
			String insertFaq = StringUtil.replace(sql, "${show_name}", show_name);
			insertFaq = StringUtil.replace(insertFaq, "${lable}", lable);
			insertFaq = StringUtil.replace(insertFaq, "${content}", content);
			//批处理插入t_faq
			db.addBatchCommand(insertFaq);
		}
		db.exec();
		
		return rc;
	}
}
