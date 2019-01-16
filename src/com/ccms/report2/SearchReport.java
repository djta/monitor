package com.ccms.report2;

import java.sql.Types;
import java.util.regex.Pattern;

import dinamica.GenericTransaction;
import dinamica.Recordset;
import dinamica.RequestValidationException;
import dinamica.StringUtil;
import dinamica.TemplateEngine;
import dinamica.ValidatorUtil;

/**
 * Motor de busqueda, construye un SQL condicionalmente de acuerdo a los
 * parametros recibidos, ejecuta el query y retorna 0 o 1 dependiendo de si el
 * recordset tiene o no registros. Esta clase dejara el recordset en sesion para
 * que pueda mostrarse en una vista paginada. <br>
 * <br>
 * Actualizado: 2007-06-27<br>
 * Framework Dinamica - Distribuido bajo licencia LGPL<br>
 * 
 * @author mcordova (martin.cordova@gmail.com)
 * */
public class SearchReport extends GenericTransaction {
	// define el ID del recordset a publicar
	// String _rsName = "query.sql"; //get rsname from config.xml

	// ghost @Override
	public int service(Recordset inputs) throws Throwable {
		int rc = super.service(inputs);

		RequestValidationException errors = new RequestValidationException();
		String dateFormat = "yyyy-MM-dd";
		String datetimeFormat = "yyyy-MM-dd HH:mm:ss";

		// String colName = getConfig().getConfigValue("colname"); //get filter
		// field 如果只有一个标签,简单.
		// String operatorName = getConfig().getConfigValue("operator"); //get
		// operator var
		String sqlFile = getConfig().getConfigValue("sql-template", "query-base.sql");
		String claseFile = getConfig().getConfigValue("clase-template", "clause-field.sql");
		String pagingRecordsetName = getConfig().getConfigValue("paging-recordset", "query-report-result.sql");

		/* ensamblar query */
		// carga el template base del query
		String qBase = getResource(sqlFile);
		String qClase = getResource(claseFile);

		// aqui se almacenaran las condiciones del WHERE
		StringBuffer qFilter = new StringBuffer();

		/* listo el query - quedo ensamblado */
		String qreport = getSQL(getResource("query_report.sql"), inputs);
		Recordset rsReport = getDb().get(qreport);

		String qfield = getSQL(getResource("query_filter_field.sql"), inputs);
		Recordset rsField = getDb().get(qfield);

		String qfieldGrid = getSQL(getResource("query_grid_field.sql"), inputs);
		rsReport.first();
		
		String is_sql_phrase = rsReport.getString("is_sql_phrase");//报表SQL语句类型 0：存储过程 1：SQL语句
		
		qfieldGrid = getSQL(qfieldGrid, rsReport);		//replace domain_value_show_type
		Recordset rsFieldGrid = getDb().get(qfieldGrid);

		String qtable = getSQL(getResource("query_table.sql"), inputs);
		Recordset rsTable = getDb().get(qtable);

		String qgroupby = getSQL(getResource("query_group_by.sql"), inputs);
		Recordset rsGroupBy = getDb().get(qgroupby);

		String qorderby = getSQL(getResource("query_order_by_field.sql"), inputs);
		Recordset rsOrderBy = getDb().get(qorderby);

		// 检测字段合法性
		rsField.top();
		while (rsField.next()) {
			String field = "";
			String value = "";
			String type = "";
			int sqlType = Types.VARCHAR;
			String label = null;
			String required = "";
			int maxLen = 0;
			String regexp = null;
			String regexpError = null;
			String showType = null;

			field = rsField.getString("field_code_alias") + "_filter";
			type = rsField.getString("field_type");
			required = rsField.getString("is_required");
			//2 为 checkbox 多选
			showType = rsField.getString("show_type");
			if (required == null){
				required = "1";
			}

			label = rsField.getString("field_name");
			if (rsField.getString("field_length") != null){
				maxLen = Integer.parseInt(rsField.getString("field_length"));
			}

			regexp = rsField.getString("format_mark");
			regexpError = "输入格式错误:" + regexp;

			/* validate type attribute */
			if (type.equalsIgnoreCase("varchar")){
				sqlType = Types.VARCHAR;
			}else if (type.equalsIgnoreCase("integer") || type.equalsIgnoreCase("int4")){
				sqlType = Types.INTEGER;
			}else if (type.equalsIgnoreCase("double") || type.equalsIgnoreCase("numeric")){
				sqlType = Types.DOUBLE;
			}else if (type.equalsIgnoreCase("date")){
				sqlType = Types.DATE;
			}else if (type.equalsIgnoreCase("timestamp")){
				sqlType = Types.TIMESTAMP;
			}

			String data[] = getRequest().getParameterValues(field);

			if (data != null && !data[0].trim().equalsIgnoreCase("")) {
				/* only accept single value parameters - not arrays */
				if (data.length == 1) {
					/* 校验数据 */
					value = data[0].trim();

					/* check maxlen rule */
					if (maxLen > 0) {
						if (value.length() > maxLen && !"2".equals(showType)){//当多选时不考虑长度
							errors.addMessage(field, label + ": " + "${lbl:data_too_long}" + maxLen);
						}
					}

					/* check regular expression */
					if (sqlType == Types.VARCHAR && regexp != null) {
						boolean isMatch = Pattern.matches(regexp, value);
						if (!isMatch){
							errors.addMessage(field, label + ": " + regexpError);
						}
					}

					/* convert to datatype if valid */
					switch (sqlType) {
						case Types.DATE:
						case Types.TIMESTAMP:
							java.util.Date d = ValidatorUtil.testDate(value, sqlType == Types.DATE ? dateFormat : datetimeFormat);
							if (d == null){
								errors.addMessage(field, label + ": " + "${lbl:invalid_date}");
							}
							break;
						case Types.DOUBLE:
							Double dbl = ValidatorUtil.testDouble(value);
							if (dbl == null){
								errors.addMessage(field, label + ": " + "${lbl:invalid_double}");
							}
							break;
						case Types.INTEGER:
							Integer i = ValidatorUtil.testInteger(value);
							if (i == null){
								errors.addMessage(field, label + ": " + "${lbl:invalid_integer}");
							}
							break;
						case Types.VARCHAR:
							break;
					}
				}
			} else {
				if (required.equalsIgnoreCase("0")){ /* 非空检测 */
					errors.addMessage(field, rsField.getString("field_name") + ": " + "${lbl:parameter_required}");
				}
			}
		}

		/* 输出错误提示 */
		if (errors.getErrors().getRecordCount() > 0) {
			getRequest().setAttribute("dinamica.error.validation", "/action/error/validation/ajax");
			getRequest().setAttribute("dinamica.errors", errors.getErrors());
			throw errors;
		}

		String field_alias = "";
		String value = "";

		
		if("1".equals(is_sql_phrase)){//SQL语句
			// 检测参数表中参数
			rsField.top();
			while (rsField.next()) {
				field_alias = rsField.getString("field_code_alias");
				if (field_alias == null){
					break;
				}
				//2 为 checkbox 多选
				String showType = rsField.getString("show_type");
				
				String data[] = getRequest().getParameterValues(field_alias + "_filter");
				String data_operator[] = getRequest().getParameterValues(field_alias + "_operator");
				if (data == null || data_operator == null) {
					continue;
				}
	
				if (data != null && !data[0].trim().equals("") || data_operator[0].equalsIgnoreCase("is not null") || data_operator[0].equalsIgnoreCase("is null")) {
					/* only accept single value parameters - not arrays */
					if (data.length == 1 && !"2".equalsIgnoreCase(showType)) {
						// 如果为空或非空
						String strTmp = qClase;
						if (data_operator[0].equalsIgnoreCase("is not null") || data_operator[0].equalsIgnoreCase("is null")) {
							strTmp = StringUtil.replace(strTmp, "${fld:colname_mark}", "");
						}
						TemplateEngine t = new TemplateEngine(getContext(), getRequest(), strTmp);
						t.replace(rsField, "");
						qFilter.append(StringUtil.replace(t.toString(), "${operator}", data_operator[0].trim()));
	
					}
					//单独处理checkbox 多选情况
					if (data.length > 0 && (data_operator[0].equalsIgnoreCase("in") || data_operator[0].equalsIgnoreCase("not in")) && "2".equalsIgnoreCase(showType)) {
						String strTmp = qClase;
						String values = "";
						for(int i=0;i<data.length;i++){
							values += data[i]+",";
						}
						values = values.substring(0,values.length()-1);
						strTmp = StringUtil.replace(strTmp, "${fld:colname_mark}", "("+values+")");
						
						TemplateEngine t = new TemplateEngine(getContext(), getRequest(), strTmp);
						t.replace(rsField, "");
						qFilter.append(StringUtil.replace(t.toString(), "${operator}", data_operator[0].trim()));
					}
				}
			}
		}
		rsTable.first();
		String table = rsTable.getString("table_code");
		String delete_field = rsTable.getString("delete_field");
		String report_sql = rsTable.getString("report_sql");
		String fields = buildGridArrayRecordset(rsFieldGrid);
		String group_by = buildGroupByArrayRecordset(rsGroupBy);
		String order_by = buildOrderByArrayRecordset(rsOrderBy);

		if (!(report_sql == null || report_sql.equalsIgnoreCase(""))){
			qBase = StringUtil.replace(report_sql, "${DEF", "${def");
			qBase = StringUtil.replace(qBase, "${FLD", "${fld");
			qBase = StringUtil.replace(qBase, "${SES", "${ses");
			qBase = StringUtil.replace(qBase, "${LBL", "${lbl");
		}

		qBase = StringUtil.replace(qBase, "${table}", table);
		qBase = StringUtil.replace(qBase, "${delete_field}", delete_field);
		qBase = StringUtil.replace(qBase, "${field}", fields);
		qBase = StringUtil.replace(qBase, "${group_by}", group_by);
		qBase = StringUtil.replace(qBase, "${order_by}", order_by);
		qBase = StringUtil.replace(qBase, "${filter}", qFilter.toString());

		// 生成暂存变量值的结果集
		Recordset detail = new Recordset();
		rsField.top();
		while (rsField.next()) {
			field_alias = rsField.getString("field_code_alias");
			detail.append(field_alias + "_filter", java.sql.Types.VARCHAR);
		}

		detail.addNew();

		// 取参数变量值
		rsField.top();
		while (rsField.next()) {
			field_alias = rsField.getString("field_code_alias");
			if (field_alias == null) {
				rc = 1;
				return rc;
			}

			String data[] = getRequest().getParameterValues(field_alias + "_filter");
			String data_operator[] = getRequest().getParameterValues(field_alias + "_operator");

			if (data != null && !data[0].trim().equals("")) {
				/* only accept single value parameters - not arrays */
				if (data.length == 1) {
					value = data[0].trim();
					if (data_operator[0].trim().equals("like")) {
						value = "%" + value + "%";
					}

					detail.setValue(field_alias + "_filter", value);

				}
			} else
				if("1".equals(is_sql_phrase)){//SQL语句
					detail.setValue(field_alias + "_filter", "%");
				}else{
					detail.setValue(field_alias + "_filter", null);
				}

		}

		String sql = getSQL(qBase, inputs);
		sql = getSQL(sql, detail);

		Recordset rs = getDb().get(sql);

		if (rs.getRecordCount() > 0) {
			getSession().setAttribute(pagingRecordsetName, rs);
			rc = 0;
		} else {
			// rs.addNew(); /*add a blank row*/
			getSession().setAttribute(pagingRecordsetName, rs);
			rc = 1;
		}

		return rc;

	}

	String buildGridArrayRecordset(Recordset rs) throws Throwable {
		String field = "";
		rs.top();
		while (rs.next()) {
			field = field + rs.getString("field_code") + " as " + rs.getString("colname") + ",";
		}
		if (field.endsWith(",")){
			field = field.substring(0, field.length() - 1);
		}
		return field;
	}

	String buildGroupByArrayRecordset(Recordset rs) throws Throwable {
		String field = "";
		rs.top();
		while (rs.next()) {
			if (field.equals("")){
				field = "group by ";
			}
			field = field + rs.getString("field_code") + ",";
		}
		if (field.endsWith(",")){
			field = field.substring(0, field.length() - 1);
		}
		return field;
	}

	String buildOrderByArrayRecordset(Recordset rs) throws Throwable {
		String field = "";
		rs.top();
		while (rs.next()) {
			if (field.equals("")){
				field = "order by ";
			}
			field = field + rs.getString("field_code") + ",";
		}
		if (field.endsWith(",")){
			field = field.substring(0, field.length() - 1);
		}
		return field;
	}

}
