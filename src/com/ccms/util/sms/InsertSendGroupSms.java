package com.ccms.util.sms;

import java.io.File;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

import jxl.JXLException;
import jxl.Sheet;
import jxl.Workbook;
import dinamica.Config;
import dinamica.Db;
import dinamica.GenericTableManager;
import dinamica.Recordset;
import dinamica.xml.Document;
import dinamica.xml.Element;

public class InsertSendGroupSms extends GenericTableManager {
	public int service(Recordset inputParams) throws Throwable {
		int rc = super.service(inputParams);
		Db db = getDb();
		Workbook wb = null;
		if (inputParams.isNull("file.filename"))
			throw new Throwable("导入文件不能为空!");
		String file = inputParams.getString("file");
		try {
			wb = Workbook.getWorkbook(new File(file));
		} catch (JXLException e1) {
			throw new Throwable("文件格式不正确", e1);
		}
		try {
			HashMap<String, Recordset> queryTogether = null;
			Document doc = getConfig().getDocument();
			// insert 插入4个字段，分别为cust_code，receiver，account_id，content
			String insertSql = "";

			String cmd1 = getResource(doc.getElement("inserts").getAttribute(
					"sql"));
			// 模板内容
			String content = formatRequestEncoding(inputParams
					.getString("content"));
			String contentStr = "";

			Sheet sheet = wb.getSheet(0);
			Element[] inserts = doc.getElements("//" + "inserts" + "/insert");
			Element[] replaces = doc
					.getElements("//" + "replaces" + "/replace");
			int numOfRows = sheet.getRows();
			for (int i = 1; i < numOfRows; i++) {
				//如果excel某一行没有值则退出
				if (check(sheet, i)) {
					break;
				}
				insertSql = cmd1;
				// 替换inserts标识
				queryTogether = new HashMap<String, Recordset>();
				contentStr = content;
				for (int j = 0; j < inserts.length; j++) {
					Recordset queryRecordset = null;
					String id = inserts[j].getAttribute("id");
					String by = inserts[j].getAttribute("by");
					String sql = inserts[j].getAttribute("sql");
					//1.如果by等于空，直接inputParams中获得值，进行替换
					if (null == by) {
						insertSql = StringUtils.replace(insertSql, "${" + id
								+ "}", inputParams.getString(id));
					} else {
						String[] splits = by.split(";");
						//2.如果by不空，sql等于空，用excel中的值进行替换
						if (null == sql) {
							String cellValue = sheet.getCell(
									Integer.valueOf(by), i).getContents();
								insertSql = StringUtils.replace(insertSql, "${"
										+ id + "}", null==cellValue?"":cellValue);
						} else {
							//3.如果by不空，sql不等于空，用sql查询进行替换
							String sqlName = inserts[j].getAttribute("sql");
							//3.1 如果用过sql查找过，直接从queryRecordset中获得结果集进行替换
							if (queryTogether.containsKey(sqlName)) {
								queryRecordset = queryTogether.get(sqlName);
							} else {
								//3.2 如果没用过sql查找过，拼接sql，进行查找，并将结果集放到queryRecordset里，以备以后使用
								String querySql = getResource(sqlName);
								for (int k = 0; k < splits.length; k++) {
									String index = splits[k]
											.substring(splits[k].indexOf("-") + 1);
									String replaceStr = splits[k].substring(0,
											splits[k].indexOf("-"));
									String cellValue = sheet.getCell(
											Integer.valueOf(index), i)
											.getContents();
									querySql = StringUtils.replace(querySql,
											"${" + replaceStr + "}", cellValue);
								}
								queryRecordset = db.get(getSQL(querySql,inputParams));
								queryTogether.put(sqlName, queryRecordset);
							}
							try {
								//4.如果结果集为空，插入空值
								queryRecordset.first();
								String value = queryRecordset.getString(id);
								insertSql = StringUtils.replace(insertSql, "${"
										+ id + "}", value);

							} catch (Throwable e) {
								insertSql = StringUtils.replace(insertSql, "${"
										+ id + "}", "");
							}
						}
					}
				}

				// 5.替换replaces标识
				for (int p = 0; p < replaces.length; p++) {
					String id = replaces[p].getAttribute("id");
					String by = replaces[p].getAttribute("by");
					String sql = replaces[p].getAttribute("sql");
					//5.1 如果sql不为空，则从queryTogether取对应的Recordset，并进行替换
					if (null != sql && !"".equals(sql)) {
						contentStr = StringUtils.replace(contentStr, id,
								queryTogether.get(sql).getString(by));
					} else {
					//5.2 如果sql为空，則取excel中的值進行替換
						contentStr = StringUtils.replace(contentStr, id, sheet
								.getCell(Integer.valueOf(by), i).getContents());
					}
				}
				insertSql = StringUtils.replace(insertSql, "${content}",
						contentStr);
				// System.out.println(getSQL(insertSql, null));
				db.addBatchCommand(getSQL(insertSql, null));
			}
			db.exec();
		} catch (Throwable e) {
			if (null == e.getMessage() || "".equals(e.getMessage())) {
				throw new Throwable("批量保存失败");
			} else {
				throw new Throwable(e.getMessage());
			}
		}
		return rc;
	}

	/**
	 * 中文编码
	 * 
	 * @param str
	 * @return
	 * @throws Throwable
	 */
	public String formatRequestEncoding(String str) throws Throwable {

		Config _config = getConfig();

		String encoding = getContext().getInitParameter("request-encoding");
		if (encoding != null && encoding.trim().equals(""))
			encoding = null;

		if (_config.requestEncoding != null)
			return new String(str.getBytes("ISO8859-1"),
					_config.requestEncoding);
		else if (encoding != null)
			return new String(str.getBytes("ISO8859-1"), encoding);
		else
			return str;
	}

	/**
	 * 校验excel表格是否为空行
	 * 
	 * @return
	 */
	public boolean check(Sheet sheet, int row) {
		boolean b = true;
		for (int i = 0; i < sheet.getColumns(); i++) {
			String value = sheet.getCell(i, row).getContents();
			if (null != value && !"".equals(value)) {
				b = false;
				break;
			}
		}
		return b;
	}
}
