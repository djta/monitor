package com.ccms.imp;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;

public class ExcelImportFor2k3 implements ExcelImportUtil{

	private List<List<String>> data = new ArrayList<List<String>>();
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//格式化日期
	private DecimalFormat df = new DecimalFormat("#.##");
	
	public ExcelImportFor2k3(File file) throws Throwable {
		HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(file));
		HSSFSheet sheet = wb.getSheetAt(0);
		int numOfRows = sheet.getPhysicalNumberOfRows();
		for(int j=0; j<numOfRows; j++)
		{
			HSSFRow dataRow = sheet.getRow(j);
			List<String> list = new ArrayList<String>();
			int numOfCols = dataRow.getLastCellNum();
			for(int i=0 ; i<numOfCols; i++)
			{
				Cell colcell = dataRow.getCell(i);
				String content = getCellValue(colcell);
				list.add(i, content);
			}
			data.add(j, list);
		}
		sheet = null;
		wb = null;
	}
	
	public String getCellValue(Cell colcell){
		String content = null;
		if(colcell != null){
			switch (colcell.getCellType()) {
			case Cell.CELL_TYPE_STRING:
				content = colcell.getStringCellValue();
				break;
			case Cell.CELL_TYPE_NUMERIC:
				if (DateUtil.isCellDateFormatted(colcell)) {
					Date dateCell = colcell.getDateCellValue();
					content = sdf.format(dateCell);
				}else {
					Double value = colcell.getNumericCellValue(); 
                    if(value == value.longValue()){ 
                    	content = String.valueOf(value.longValue());
                    }else{
                    	content = df.format(value);
                    }
				}
				break;
			case Cell.CELL_TYPE_FORMULA:
				content = colcell.getCellFormula();
				break;
			case Cell.CELL_TYPE_BOOLEAN:
				content = String.valueOf(colcell.getBooleanCellValue());
				break;
			case Cell.CELL_TYPE_BLANK:
				content = colcell.getStringCellValue();
				break;
			case Cell.CELL_TYPE_ERROR:
				content = String.valueOf(colcell.getErrorCellValue());
				break;
			default:
				content = colcell.toString();
			}
		}
		return content;
	}
	
	@Override
	public List<List<String>> getExcelData() {
		return data;
	}
	
}
