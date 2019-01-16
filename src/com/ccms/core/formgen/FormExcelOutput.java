package com.ccms.core.formgen;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;

import javax.sql.DataSource;
import javax.xml.xpath.XPathExpressionException;

import jxl.Workbook;
import jxl.write.DateFormat;
import jxl.write.DateTime;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import com.ccms.caches.CacheProvider;
import com.ccms.caches.impl.CacheProviderImpl;
import com.ccms.core.foctory.DocumentBean;
import com.ccms.core.foctory.FormBean;
import com.ccms.dialect.Dialect;
import com.ccms.dialect.DialectFactory;

import dinamica.AbstractExcelOutput;
import dinamica.Db;
import dinamica.GenericTransaction;
import dinamica.Jndi;
import dinamica.Recordset;
import dinamica.RecordsetField;
import dinamica.StringUtil;
import dinamica.TemplateEngine;
import dinamica.xml.Document;

public class FormExcelOutput extends AbstractExcelOutput {

	public WritableWorkbook createWorkbook(GenericTransaction data, ByteArrayOutputStream buf) throws Throwable {

		String suffix = getConfig().getConfigValue("suffix-col", "document_id");
		String suffixValue = getRequest().getParameter(suffix);

		String dateFmt = getContext().getInitParameter("def-format-date");

		Document doc = getConfig().getDocument();
		String customDateFmt = doc.getElement("excel").getAttribute("date-format");
		if (customDateFmt == null)
			customDateFmt = dateFmt;

		WritableWorkbook wb = Workbook.createWorkbook(buf);
		WritableCellFormat dateFormat = new WritableCellFormat(new DateFormat(customDateFmt));

		String querySql = (String)getRequest().getSession().getAttribute(doc.getElement("excel").getAttribute("recordset") + "_" + (suffixValue == null ? "" : suffixValue));
		if(querySql != null && querySql.length() > 0){
			//获取数据库连接
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
				conn = ds.getConnection();
				Db db = new Db(conn);
			
				//每次都从缓存中重新复制一份
				CacheProvider cp = new CacheProviderImpl();
				DocumentBean document = cp.getDocumentBeanById(Integer.parseInt(suffixValue));
				Integer form_id = document.getForm_id();
				FormBean form = cp.getFormBeanById(form_id);
				Recordset rows = form.getQueryExcelField().copy();
				
				//当前语言
				String locale = getRequest().getSession().getAttribute("dinamica.user.locale").toString();
				int recordcount = Integer.parseInt(getRequest().getParameter("recordcount"));
				
				//记录审计数据(把导出的总记录数计做主键值)
				String excelAudit = form.getExcel_audit_sql();
				excelAudit = StringUtil.replace(excelAudit, "${pk_value}", String.valueOf(recordcount));
				TemplateEngine te = new TemplateEngine(getContext(), getRequest(), excelAudit);
				te.getSql(null);//替换序列
				db.exec(te.toString());
				
				//需要考虑分Sheet页导出，每页限定5万条。
				Integer currPage = 1;
				Integer totalPage = 0;
				Integer pageSize = 50000;
				totalPage = ((recordcount-1)/pageSize)+1;
				
				Dialect dialect = DialectFactory.buildDialect(conn.getMetaData().getDatabaseProductName().toLowerCase());
				for(int j=1;j<=totalPage;j++){
					String sql = dialect.getLimitString(querySql, pageSize*(currPage-1), pageSize);
					Recordset rs = db.get(sql);
					WritableSheet sheet = wb.createSheet(doc.getElement("excel").getAttribute("sheetname")+j, j-1);
					
					int count = sheet.getRows();
					for (int i = 0; i < rows.getRecordCount(); i++) {
						rows.setRecordNumber(i);
						Label label = new Label(i, count, rows.getString("field_name_"+locale));
						sheet.addCell(label);
					}
					while (rs.next()) {
						count = sheet.getRows();
						for (int i = 0; i < rows.getRecordCount(); i++) {
							rows.setRecordNumber(i);
							String colName = rows.getString("colname");
		
							if (rs.isNull(colName)) {
								Label label = new Label(i, count, "");
								sheet.addCell(label);
							} else {
								RecordsetField rf = rs.getField(colName);
								switch (rf.getType()) {
		
								case java.sql.Types.DATE:
									DateTime date = new DateTime(i, count, rs.getDate(colName), dateFormat);
									sheet.addCell(date);
									break;
		
								case java.sql.Types.INTEGER:
								case java.sql.Types.DOUBLE:
									Number number = new Number(i, count, rs.getDouble(colName));
									sheet.addCell(number);
									break;
		
								default:
									Label label = new Label(i, count, rs.getString(colName));
									sheet.addCell(label);
									break;
								}
							}
						}
					}
					
					//页数增加
					currPage ++;
				}
			}
			catch (Throwable e)
			{
				throw e;
			}
			finally{
				if(conn != null){
					conn.close();
				}
			}
		}
		
		wb.write();
		wb.close();

		return wb;

	}

	protected String getAttachmentString() {
		String fileName = "data";
		try {
			fileName = getConfig().getDocument().getElement("excel").getAttribute("filename");
			if (getRequest().getHeader("user-agent").indexOf("MSIE") != -1) {
				fileName = java.net.URLEncoder.encode(fileName, "utf-8");
				fileName = fileName.replaceAll("\\+", "%20"); // 处理空格
			} else {
				fileName = new String(fileName.getBytes("utf-8"), "iso-8859-1");
			}
		} catch (UnsupportedEncodingException e) {
		} catch (XPathExpressionException e) {
		}
		return "attachment; filename=\"" + fileName + "\";";
	}

	protected WritableSheet beforeData(WritableSheet sheet, GenericTransaction data, int countReg) throws Throwable {
		return sheet;
	}

	protected WritableSheet afterData(WritableSheet sheet, GenericTransaction data, int countReg) throws Throwable {
		return sheet;
	}
}
