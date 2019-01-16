package com.ccms.imp;

import java.io.ByteArrayOutputStream;

import javax.xml.xpath.XPathExpressionException;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import dinamica.AbstractExcelOutput;
import dinamica.GenericTransaction;
import dinamica.Recordset;
import dinamica.xml.Document;

/**
 * @author zc
 * 导出导入的错误数据到Excel
 *
 */
public class ExcelErrorDataOutput extends AbstractExcelOutput
{
	public WritableWorkbook createWorkbook(GenericTransaction data, ByteArrayOutputStream buf) throws Throwable
	{

		String dateFmt = getContext().getInitParameter("def-format-date");
		
        //obtener referencia a config.xml
		Document doc = getConfig().getDocument();
		String customDateFmt = doc.getElement("excel").getAttribute("date-format"); 
		if (customDateFmt==null)
			customDateFmt = dateFmt;
		
		//crear el workbook
		WritableWorkbook wb = Workbook.createWorkbook(buf);
		WritableSheet sheet = wb.createSheet(doc.getElement("excel").getAttribute("sheetname"), 0);

        beforeData(sheet, data, 0);
        
        //obtener recordset de data
	    Recordset rs = data.getRecordset(doc.getElement("excel").getAttribute("data_recordset"));
	    Recordset rsTitle = data.getRecordset(doc.getElement("excel").getAttribute("title_recordset"));
	    
	    //设置标题行
    	rsTitle.top();
    	int c = 0;
    	while(rsTitle.next()){
    		Label label = new Label( c, 0, rsTitle.getString("col_name") ); 
			sheet.addCell(label);
			c ++;
    	}
	    
	    int count = 0;
	    rs.top();
        while (rs.next())
	    {
        	String error = rs.getString("error");
        	if(error == null || error.length()==0){
        		continue;
        	}
        	
        	count ++;
        	
        	c = 0;
        	rsTitle.top();
        	while(rsTitle.next()){
        		String code = rsTitle.getString("col_code");
        		Label label = new Label( c, count, rs.getString(code) ); 
    			sheet.addCell(label);
    			c ++;
        	}
	    }
        
        afterData(sheet, data, count);
        
        wb.write();
        wb.close(); 
        
        //retornar documento para su impresion hacia el browser
        return wb;
		
	}

	@Override
	protected String getAttachmentString() {
		
		String fileName = "data.xls";
		try {
			fileName = getConfig().getDocument().getElement("excel").getAttribute("filename");
		} catch (XPathExpressionException e) {
		}
		return "attachment; filename=\"" + fileName + "\";";
	}
	
	/**
	 * Metodo que permite a�adir data a la hoja de calculo antes
	 * de la imprimir la data del detalle, es especial para los casos
	 * cuando se desea imprimir titulos antes de la data. 
	 * @param sheet Hoja de calculo
	 * @param data Objeto Transaction que contiene los recordsets del Action
	 * @param countReg Row donde esta posicionada la hoja de calculo
	 * @return sheet Hoja de calculo
	 * @throws Throwable
	 */
	protected WritableSheet beforeData (WritableSheet sheet, GenericTransaction data, int countReg) throws Throwable {
		return sheet;
	}
	
	/**
	 * Metodo que permite a�adir data a la hoja de calculo despues
	 * de la imprimir la data del detalle, es especial para los casos
	 * cuando se desea imprimir un total. 
	 * @param sheet Hoja de calculo
	 * @param data Objeto Transaction que contiene los recordsets del Action
	 * @param countReg Row donde esta posicionada la hoja de calculo
	 * @return sheet Hoja de calculo
	 * @throws Throwable
	 */
	protected WritableSheet afterData (WritableSheet sheet, GenericTransaction data, int countReg) throws Throwable {
		return sheet;
	}
}
