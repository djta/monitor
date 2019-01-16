package com.ccms.report;

import dinamica.*;

import javax.sql.DataSource;

import java.sql.*;

/**
 * Class description. <br>
 * <br>
 * (c) 2004 Martin Cordova<br>
 * This code is released under the LGPL license<br>
 * Dinamica Framework - http://www.martincordova.com
 * 
 * @author Martin Cordova (dinamica@martincordova.com)
 * */
public class ReportFilterOutput extends GenericOutput {

	/**
	 * 每行多少列
	 */
	public static final int totalColOneRow = 12;
	
	public void print(TemplateEngine te, GenericTransaction data) throws Throwable {

		super.print(te, data);

		String jndiPrefix = getContext().getInitParameter("jndi-prefix");
		String dataSourceName = getContext().getInitParameter("def-datasource");

		if (getConfig().transDataSource != null)
			dataSourceName = getConfig().transDataSource;

		if (jndiPrefix == null)
			jndiPrefix = "";

		DataSource ds = Jndi.getDataSource(jndiPrefix + dataSourceName);

		Connection conn = null;

		try {
			if (this.getConnection() == null) {
				// connect to database
				conn = ds.getConnection();
				this.setConnection(conn);
			}

			// 加载模版
			String tCheckbox = getResource(getConfig().getConfigValue("checkbox-template"));
			String tRadiobox = getResource(getConfig().getConfigValue("radio-template"));
			String tText = getResource(getConfig().getConfigValue("text-template"));
			String tCombo = getResource(getConfig().getConfigValue("combo-template"));
			String tDate = getResource(getConfig().getConfigValue("date-template"));
			String tDatetime = getResource(getConfig().getConfigValue("datetime-template"));
			String tHidden = getResource(getConfig().getConfigValue("hidden-template"));
			String tTextReadonly = getResource(getConfig().getConfigValue("textreadonly-template"));
			String tTextarea = getResource(getConfig().getConfigValue("textarea-template"));
			String tPickup = getResource(getConfig().getConfigValue("pickup-template"));
			String tPlugin = getResource(getConfig().getConfigValue("plugin-template"));

			String strDomain = getResource(getConfig().getConfigValue("domainsql-template"));
			String strFkField = getResource(getConfig().getConfigValue("fk-field-sql-template"));
			String strFkData = getResource(getConfig().getConfigValue("fk-data-sql-template"));

			TemplateEngine tData = new TemplateEngine(getContext(), getRequest(), strFkData);
			strFkData = tData.toString();

			String tRow = getResource(getConfig().getConfigValue("row-template"));
			String rsName = getConfig().getConfigValue("filter-field");
			String rsTableName = getConfig().getConfigValue("table-recordset");

			Recordset rs = data.getRecordset(rsName);
			Recordset rsTable = data.getRecordset(rsTableName);
			rsTable.first();

			Integer iShowType = 0;
			int colTotal = 0; // 当前列总数
			//存储整个table
			StringBuffer hgrid = new StringBuffer(256);
			//存储hidden字段
			StringBuffer hgridHidden = new StringBuffer(256);
			StringBuilder colsBuf = new StringBuilder(256);
			rs.top();
			while (rs.next()) {
				if (rs.getString("show_type") != null)
					iShowType = rs.getInteger("show_type");
				else
					iShowType = 0; /* 默认文本框 */

				if (iShowType == 9) {
					hgridHidden.append(getHidden(rs, tHidden));
					continue;
				}
				int col = rs.getInt("colspan");
				colTotal += col;

				switch (iShowType.intValue()) {
				case 0:
					colsBuf.append(getText(rs, tText));
					break;
				case 1:
					colsBuf.append(getCombo(rs, tCombo, strDomain, strFkField, strFkData));
					break;
				case 2:
					colsBuf.append(getCheckbox(rs, tCheckbox));
					break;
				case 3:
					colsBuf.append(getRadio(rs, tRadiobox, strDomain, strFkField, strFkData));
					break;
				case 4:
					colsBuf.append(getReadonly(rs, tTextReadonly));
					break;
				case 5:
					colsBuf.append(getDate(rs, tDate));
					break;
				case 6:
					colsBuf.append(getTextarea(rs, tTextarea));
					break;
				case 7:
					colsBuf.append(getPickup(rs, tPickup));
					break;
				case 8:
					colsBuf.append(getPlugin(rs, getPlugin(rs, tPlugin)));
					break;
				case 10:
					colsBuf.append(getDate(rs, tDatetime));
					break;
				default:
					colsBuf.append(getText(rs, tText));
					break;
				}
				if (colTotal >= totalColOneRow) {
					hgrid.append(StringUtil.replace(tRow, "${cols}", colsBuf.toString()));
					colsBuf.delete(0, colsBuf.length());
					colTotal = 0;
				}
			}
			if(colTotal > 0 && colTotal <= totalColOneRow){
				hgrid.append(StringUtil.replace(tRow, "${cols}", colsBuf.toString()));
				colsBuf.delete(0, colsBuf.length());
				colTotal = 0;
			}
			hgrid.append(hgridHidden);
			te.replace(rsTable, "");
			te.replace("${controls}", hgrid.toString());

		} catch (Throwable e) {
			throw e;
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (Throwable e) {
				throw e;
			}
		}

	}

	String getText(Recordset rs, String te) throws Throwable {
		TemplateEngine t = new TemplateEngine(getContext(), getRequest(), te);
		t.replace(rs, "");
		return t.toString();
	}

	String getCombo(Recordset rs, String te, String domain, String strFkField, String strFkData) throws Throwable {

		Db db = getDb();
		// 取域值
		TemplateEngine t1 = new TemplateEngine(getContext(), getRequest(), domain);
		t1.replace(rs, "");
		Recordset rsDomain = db.get(t1.toString());

		TemplateEngine t = new TemplateEngine(getContext(), getRequest(), te);
		t.replace(rs, "");
		if (rsDomain.getRecordCount() > 0) {
			t.replace(rsDomain, "", "rows");
		} else {
			TemplateEngine t2 = new TemplateEngine(getContext(), getRequest(), strFkField);
			t2.replace(rs, "");
			Recordset rsFkFiled = db.get(t2.toString());
			if (rsFkFiled.next()) {
				rsFkFiled.first();
				String schema = rsFkFiled.getString("schema_code");
				String table = rsFkFiled.getString("table_code");
				String field = rsFkFiled.getString("field_code");
				String field_alias = rsFkFiled.getString("field_alias");
				String fk_references = rsFkFiled.getString("fk_references");
				String fk_sql = rsFkFiled.getString("fk_sql");

				// 外键配置数据不完整
				if (table == null || field == null || field_alias == null) {
					return t.toString();
				}
				if (fk_sql != null && !"".equals(fk_sql)) {
					fk_sql = StringUtil.replace(fk_sql, "${DEF", "${def");
					String table_alias = "(" + fk_sql + ") as " + table;
					strFkData = StringUtil.replace(strFkData, "${schema}", "");
					strFkData = StringUtil.replace(strFkData, "${table}", table_alias);
				} else {
					strFkData = StringUtil.replace(strFkData, "${schema}", schema + ".");
					strFkData = StringUtil.replace(strFkData, "${table}", table);
				}

				strFkData = StringUtil.replace(strFkData, "${field_code}", field);
				strFkData = StringUtil.replace(strFkData, "${field_alias}", field_alias);
				strFkData = StringUtil.replace(strFkData, "${field_reference}", fk_references);

				Recordset rsFkData = db.get(strFkData);

				t.replace(rsFkData, "", "rows");
			}
		}

		return t.toString();
	}

	String getCheckbox(Recordset rs, String te) throws Throwable {
		TemplateEngine t = new TemplateEngine(getContext(), getRequest(), te);
		t.replace(rs, "");
		return t.toString();
	}

	String getRadio(Recordset rs, String te, String domain, String strFkField, String strFkData) throws Throwable {
		Db db = getDb();
		// 取域值
		TemplateEngine t1 = new TemplateEngine(getContext(), getRequest(), domain);
		t1.replace(rs, "");
		Recordset rsDomain = db.get(t1.toString());

		TemplateEngine t = new TemplateEngine(getContext(), getRequest(), te);
		t.replace(rs, "");
		if (rsDomain.getRecordCount() > 0) {
			t.replace(rsDomain, "", "rows");
		} else {
			TemplateEngine t2 = new TemplateEngine(getContext(), getRequest(), strFkField);
			t2.replace(rs, "");
			Recordset rsFkFiled = db.get(t2.toString());
			if (rsFkFiled.next()) {
				rsFkFiled.first();
				String schema = rsFkFiled.getString("schema_code");
				String table = rsFkFiled.getString("table_code");
				String field = rsFkFiled.getString("field_code");
				String field_alias = rsFkFiled.getString("field_alias");
				String fk_references = rsFkFiled.getString("fk_references");
				String fk_sql = rsFkFiled.getString("fk_sql");

				// 外键配置数据不完整
				if (table == null || field == null || field_alias == null) {
					return t.toString();
				}
				if (fk_sql != null && !"".equals(fk_sql)) {
					fk_sql = StringUtil.replace(fk_sql, "${DEF", "${def");
					String table_alias = "(" + fk_sql + ") as " + table;
					strFkData = StringUtil.replace(strFkData, "${schema}", "");
					strFkData = StringUtil.replace(strFkData, "${table}", table_alias);
				} else {
					strFkData = StringUtil.replace(strFkData, "${schema}", schema + ".");
					strFkData = StringUtil.replace(strFkData, "${table}", table);
				}

				strFkData = StringUtil.replace(strFkData, "${field_code}", field);
				strFkData = StringUtil.replace(strFkData, "${field_alias}", field_alias);
				strFkData = StringUtil.replace(strFkData, "${field_reference}", fk_references);

				Recordset rsFkData = db.get(strFkData);

				t.replace(rsFkData, "", "rows");
			}
		}

		return t.toString();
	}

	String getReadonly(Recordset rs, String te) throws Throwable {
		TemplateEngine t = new TemplateEngine(getContext(), getRequest(), te);
		t.replace(rs, "");
		return t.toString();
	}

	String getDate(Recordset rs, String te) throws Throwable {
		TemplateEngine t = new TemplateEngine(getContext(), getRequest(), te);
		t.replace(rs, "");
		return t.toString();
	}

	String getTextarea(Recordset rs, String te) throws Throwable {
		TemplateEngine t = new TemplateEngine(getContext(), getRequest(), te);
		t.replace(rs, "");
		return t.toString();
	}

	String getPickup(Recordset rs, String te) throws Throwable {
		TemplateEngine t = new TemplateEngine(getContext(), getRequest(), te);
		t.replace(rs, "");
		return t.toString();
	}

	String getPlugin(Recordset rs, String te) throws Throwable {
		TemplateEngine t = new TemplateEngine(getContext(), getRequest(), te);
		t.replace(rs, "");
		return t.toString();
	}

	String getHidden(Recordset rs, String te) throws Throwable {
		TemplateEngine t = new TemplateEngine(getContext(), getRequest(), te);
		t.replace(rs, "");
		return t.toString();
	}

}
