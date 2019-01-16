package com.ccms.util.mms;

import java.io.File;
import java.sql.Types;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jxl.JXLException;
import jxl.Sheet;
import jxl.Workbook;
import dinamica.GenericTransaction;
import dinamica.Recordset;

public class UploadPerson extends GenericTransaction {
	public int service(Recordset inputParams) throws Throwable {
		if (inputParams.isNull("file.filename"))
			throw new Throwable("导入文件不能为空!");
		String filePath = inputParams.getString("file");
		Workbook wb = null;
		try {
			wb = Workbook.getWorkbook(new File(filePath));
		} catch (JXLException e1) {
			throw new Throwable("文件格式不正确", e1);
		}
		Sheet sheet = wb.getSheet(0);
		int numOfRows = sheet.getRows();
		StringBuffer mobiles = new StringBuffer();
		for (int i = 1; i < numOfRows; i++) {
			String value = sheet.getCell(0, i).getContents();
			String regex = "13[0-9]{1}[0-9]{8}$|^15[012356789]{1}[0-9]{8}$|^18[02356789]{1}[0-9]{8}|^147{1}[0-9]{8}|^167{1}[0-9]{8}";
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(value);
			if (!m.matches()) {
				Recordset mobilesRecordset = new Recordset();
				mobilesRecordset.append("mobiles", Types.VARCHAR);
				mobilesRecordset.append("upload_error", Types.VARCHAR);
				mobilesRecordset.addNew();
				mobilesRecordset.setValue("mobiles", "");
				mobilesRecordset.setValue("upload_error", "第" + i + "行手机号码输入有误，请核实");
				publish("textnum", mobilesRecordset);
				return 0;
			}
			if (null != value && !"".equals(value)) {
				if (mobiles.length() == 0) {
					mobiles.append(value);
				} else {
					mobiles.append(",");
					mobiles.append(value);
				}
			}
		}
		//关闭流
		wb.close();
		
		Recordset mobilesRecordset = new Recordset();
		mobilesRecordset.append("mobiles", Types.VARCHAR);
		mobilesRecordset.append("upload_error", Types.VARCHAR);
		mobilesRecordset.addNew();
		mobilesRecordset.setValue("mobiles", mobiles.toString());
		mobilesRecordset.setValue("upload_error", "");
		publish("textnum", mobilesRecordset);
		return 0;
	}
}
