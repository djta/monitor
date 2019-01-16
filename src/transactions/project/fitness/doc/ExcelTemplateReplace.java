package transactions.project.fitness.doc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import dinamica.GenericTransaction;
import dinamica.Recordset;
import dinamica.StringUtil;

/***
 * 根据模板生成excel
 * @author C
 * 2016-07-15
 */
public class ExcelTemplateReplace extends GenericTransaction {

	public int service(Recordset inputParams) throws Throwable {
		int rc = super.service(inputParams);
		// 获取模板地址
		String templatePath = getConfig().getConfigValue("doc-template","");
		if( StringUtils.isBlank(templatePath) ){
			throw new Throwable("节点模板地址doc-template不能为空");
		}

		// 获取数据开始行
		Integer beginRow = 2;	// 默认第二行开始填充数据
		String beginrowStr = getConfig().getConfigValue("begin-row","");
		if( StringUtils.isNotBlank(beginrowStr) ){
			try{
				beginRow = Integer.parseInt(beginrowStr);
			}catch(NumberFormatException ex){}
		}
		
		// 获取替换参数对应的字段名(与excel次序保持一致)
		String fields = null;
		// 打印类型
		String print_type = "";
		if( inputParams.containsField("print_type") && null != inputParams.getString("print_type") ){
			print_type = inputParams.getString("print_type");
		}
		if( StringUtils.isBlank(print_type) ){
			print_type = "excel";
		}
		
		// 获取数据
		Recordset rsData = null;
		if( null != getRequest().getParameter("datatype") && "page".equals(getRequest().getParameter("datatype")) ){	// 从界面获取数据
			String paramlist = getRequest().getParameter("paramlist");	// 字段名称列表
			fields = paramlist;
			if( null != paramlist ){
				String[] paramArr = paramlist.split(";");
				int paramlen = paramArr.length;
				rsData = new Recordset();
				int datalen = 0;	// 值个数
				Map<String, String[]> map = new HashMap<String, String[]>();
				// 获取字段名列表
				for( int i = 0; i < paramlen; i++ ){
					String paramname = paramArr[i];	// 字段名
					if( null == paramname || "".equals(paramname) ){
						continue;
					}
					rsData.append(paramname, Types.VARCHAR);
					String[] paramvalue = getRequest().getParameterValues(paramname);	// 字段值
					map.put(paramname, paramvalue);
					if( i == 0 ){
						datalen = paramvalue.length;
					}
				}
				// 获取字段值
				for( int i = 0; i < datalen; i++ ){
					rsData.addNew();
					for (Map.Entry<String, String[]> entry : map.entrySet()) {  
						rsData.setValue(entry.getKey(), getArrValue(entry.getValue(), i));
					}  
				}
			}
		}else{
			fields = getConfig().getConfigValue("fields","");
			String colname = getConfig().getConfigValue("colname", "");
			String sqlFile = getConfig().getConfigValue("sql-template", "query-base.sql");
			String qBase = getResource(sqlFile);
			StringBuffer qFilter = new StringBuffer(256);

			if (colname != null && colname.length() > 0) {
				// 拼接查询条件
				String[] params = colname.split(",");
				for (int j = 0; j < params.length; j++) {
					if (inputParams.getValue(params[j]) != null) {
						qFilter.append(getResource("clause-" + params[j] + ".sql"));
					}
				}
			}
			// 替换变量
			qBase = StringUtil.replace(qBase, "${filter}", qFilter.toString());
			qBase = getSQL(qBase, inputParams);
			rsData = getDb().get(qBase);
		}

		// 获取替换参数对应的字段名(与excel次序保持一致)
		fields = ( StringUtils.isNotBlank(fields) ? fields : "" );
		String[] fieldArr = fields.split(";");
		int len = fieldArr.length;

		String basePath = getRequest().getSession().getServletContext().getRealPath("");
		
		// 获取表格模板内容
		InputStream fs = new FileInputStream(basePath + templatePath);
		XSSFWorkbook wb = new XSSFWorkbook(fs);	// 得到Excel工作簿对象
		XSSFSheet sheet = wb.getSheetAt(0);	// 得到Excel工作表对象
		// 填充数据
		int idx = beginRow;
		rsData.top();
		while(rsData.next()){
			XSSFRow row = sheet.createRow(idx);	// 得到Excel工作表的行
			for( int i = 0; i < len; i++ ){
				// 得到Excel工作表指定行的单元格
				XSSFCell cell = row.createCell(i);
				// 设置单元格样式
				XSSFCellStyle cellStyle = wb.createCellStyle();
				// 设置边框
				cellStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);	// 下边框
				cellStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);	// 左边框
				cellStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);	// 上边框
				cellStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);	// 右边框
				cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);	// 居中
				cellStyle.setWrapText(true);	// 设置自动换行
				cell.setCellStyle(cellStyle);
				
				String fieldname = fieldArr[i];	// 字段名
				if( StringUtils.isBlank(fieldname) ){
					continue;
				}
				if( "rowno".equals(fieldname) ){	// 行号
					cell.setCellValue(idx - beginRow + 1);
					continue;
				}
				Object value = rsData.getValue(fieldname);
				if( null == value ){
					cell.setCellValue("");
				}else{
					cell.setCellValue(String.valueOf(value));
				}
			}
			idx++;
		}
		
		// 获取文档新路径
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String userlogin = getRequest().getRemoteUser();
		String baseName = sdf.format(new Date()) + "_" + (null == userlogin ? "" : userlogin);
		String docOutPath = File.separator + "erpclubdoc" + File.separator + "data" + File.separator + print_type + File.separator + baseName + ".xlsx";	// 文档保存路径
		String savePath = basePath + docOutPath;

		// 文档保存
		FileOutputStream fos = null;
		try{
			fos = new FileOutputStream(new File(savePath));
			wb.write(fos);	// 写入excel文件
		}catch(IOException iex){
			iex.printStackTrace();
		}finally{
			if( null != fos ){
				fos.close(); 
			}
		}
		
		// 输出
		Recordset rsdoc = new Recordset();
		rsdoc.append("docpath", Types.VARCHAR);
		rsdoc.addNew();
		String docPath = "/erpclubdoc/data/" + print_type + "/" + baseName + ".xlsx";	// 文档保存路径
		rsdoc.setValue("docpath", docPath);
		publish("_rsdoc", rsdoc);
		return rc;
	}
	
	private static String getArrValue(String[] arr, int idx){
		String value = "";
		if( null != arr && arr.length > idx ){
			value = arr[idx];
			value = ( null != value ? value : "" );
		}
		return value;
	}
}
