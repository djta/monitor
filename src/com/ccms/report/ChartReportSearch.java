package com.ccms.report;

import java.sql.Types;
import java.util.Date;
import java.util.regex.Pattern;

import dinamica.GenericTransaction;
import dinamica.Recordset;
import dinamica.RequestValidationException;
import dinamica.StringUtil;
import dinamica.TemplateEngine;
import dinamica.ValidatorUtil;

public class ChartReportSearch extends GenericTransaction{

	public int service(Recordset inputs) throws Throwable
	{

		int rc = super.service(inputs);
		
		RequestValidationException errors = new RequestValidationException();
		String dateFormat = "yyyy-MM-dd";
		String datetimeFormat = "yyyy-MM-dd HH:mm:ss";
		
		String qfield = getSQL(getResource("query_filter_field.sql"), inputs);
		Recordset rsField = getDb().get(qfield);
		
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
			if (required == null)
				required = "1";

			label = rsField.getString("field_name");
			if (rsField.getString("field_length") != null)
				maxLen = Integer.parseInt(rsField.getString("field_length"));

			regexp = rsField.getString("format_mark");
			regexpError = "输入格式错误:" + regexp;

			/* validate type attribute */
			if (type.equalsIgnoreCase("varchar"))
				sqlType = Types.VARCHAR;
			else if (type.equalsIgnoreCase("integer") || type.equalsIgnoreCase("int4"))
				sqlType = Types.INTEGER;
			else if (type.equalsIgnoreCase("double") || type.equalsIgnoreCase("numeric"))
				sqlType = Types.DOUBLE;
			else if (type.equalsIgnoreCase("date"))
				sqlType = Types.DATE;
			else if (type.equalsIgnoreCase("timestamp"))
				sqlType = Types.TIMESTAMP;

			String data[] = getRequest().getParameterValues(field);

			if (data != null && !data[0].trim().equalsIgnoreCase("")) {
				/* only accept single value parameters - not arrays */
				if (data.length == 1) {
					/* 校验数据 */
					value = data[0].trim();

					/* check maxlen rule */
					if (maxLen > 0) {
						if (value.length() > maxLen && !"2".equals(showType))//当多选时不考虑长度
							errors.addMessage(field, label + ": " + "${lbl:data_too_long}" + maxLen);
					}

					/* check regular expression */
					if (sqlType == Types.VARCHAR && regexp != null) {
						boolean isMatch = Pattern.matches(regexp, value);
						if (!isMatch)
							errors.addMessage(field, label + ": " + regexpError);
					}

					/* convert to datatype if valid */
					switch (sqlType) {
					case Types.DATE:
						java.util.Date d = ValidatorUtil.testDate(value, dateFormat);
						if (d == null)
							errors.addMessage(field, label + ": " + "${lbl:invalid_date}");
						break;
						
					case Types.TIMESTAMP:
						java.util.Date d2 = ValidatorUtil.testDate(value, datetimeFormat);
						if (d2 == null)
							errors.addMessage(field, label + ": " + "${lbl:invalid_date}");

						break;

					case Types.DOUBLE:
						Double dbl = ValidatorUtil.testDouble(value);
						if (dbl == null)
							errors.addMessage(field, label + ": " + "${lbl:invalid_double}");

						break;

					case Types.INTEGER:
						Integer i = ValidatorUtil.testInteger(value);
						if (i == null)
							errors.addMessage(field, label + ": " + "${lbl:invalid_integer}");

						break;

					case Types.VARCHAR:
						break;

					}

				}
			} else {
				if (required.equalsIgnoreCase("0")) /* 非空检测 */
				{
					errors.addMessage(field, rsField.getString("field_name") + ": " + "${lbl:parameter_required}");
				}
			}
		}

		/* 输出错误提示 */
		if (errors.getErrors().getRecordCount() > 0) {
			getRequest().setAttribute("dinamica.error.validation", "/action/ccms/reportgen/jorsearch/validation");
			getRequest().setAttribute("dinamica.errors", errors.getErrors());
			throw errors;
		}
		
		
		String claseFile = getConfig().getConfigValue("clase-template", "clause-field.sql");
		
		
		String qClase = getResource(claseFile);

		
		StringBuffer qFilter = new StringBuffer();

		
		String qreport = getSQL(getResource("query_report.sql"), inputs);
		Recordset rsReport = getDb().get(qreport);

		rsReport.first();
		
		String is_sql_phrase = rsReport.getString("is_sql_phrase");//报表SQL语句类型 0：存储过程 1：SQL语句
		
		String qtable = getSQL(getResource("query_table.sql"), inputs);
		Recordset rsTable = getDb().get(qtable);
		rsTable.first();
		String table_code = rsTable.getString("table_code");

		
		String field_alias = "";
		String value = "";

		StringBuffer sbFilter = new StringBuffer("");
		
		if("1".equals(is_sql_phrase)){//SQL语句
			// 检测参数表中参数
			rsField.top();
			while (rsField.next()) {
				field_alias = rsField.getString("field_code_alias");
				String field_type = rsField.getString("field_type");
				if (field_alias == null)
					break;
				
				//2 为 checkbox 多选
				String showType = rsField.getString("show_type");
				String is_virtual_type = rsField.getString("is_virtual_type");
				
				String data[] = getRequest().getParameterValues(field_alias + "_filter");
				String data_operator[] = getRequest().getParameterValues(field_alias + "_operator");
				if (data == null || data_operator == null) {
					continue;
				}
	
				if (data != null && !data[0].trim().equals("")) {
					/* only accept single value parameters - not arrays */
					if (data.length == 1 && !"2".equalsIgnoreCase(showType) && !(data_operator[0].equalsIgnoreCase("in") || data_operator[0].equalsIgnoreCase("not in"))) {
						sbFilter.append("&").append(field_alias).append("=").append(data[0]);
						// 如果为空或非空
						String strTmp = qClase;
						strTmp = StringUtil.replace(strTmp, "${table_code}", "1".equals(is_virtual_type)?"":table_code+".");

						if (data_operator[0].equalsIgnoreCase("is not null") || data_operator[0].equalsIgnoreCase("is null")) {
							strTmp = StringUtil.replace(strTmp, "${fld:colname_mark}", "");
						}
						TemplateEngine t = new TemplateEngine(getContext(), getRequest(), strTmp);
						t.replace(rsField, "");
						qFilter.append(StringUtil.replace(t.toString(), "${operator}", ("llike".equalsIgnoreCase(data_operator[0])||"rlike".equalsIgnoreCase(data_operator[0]))?"like":data_operator[0]));
	
					}
					//单独处理checkbox 多选情况
					if (data.length > 0 && (data_operator[0].equalsIgnoreCase("in") || data_operator[0].equalsIgnoreCase("not in")) /*&& "2".equalsIgnoreCase(showType)*/) {
						String strTmp = qClase;
						strTmp = StringUtil.replace(strTmp, "${table_code}", "1".equals(is_virtual_type)?"":table_code+".");
						String values = "";
						for(int i=0;i<data.length;i++){
							sbFilter.append("&").append(field_alias).append("=").append(data[i]);
							values += data[i]+",";
						}
						values = values.substring(0,values.length()-1);
						if (values.endsWith(","))
							values = values.substring(0,values.length()-1);
						
						//重新组合,并判断是否加拼单引号
						String[] values_array = values.split(",");
						values = "";
						for (int i=0;i<values_array.length;i++)
						{
							//如果为varchar,则加拼单引号
							values = values+ (field_type.equalsIgnoreCase("varchar")?"'":"") + values_array[i] + (field_type.equalsIgnoreCase("varchar")?"'":"")+",";
						}

						if (values.endsWith(","))
							values = values.substring(0,values.length()-1);
						
						strTmp = StringUtil.replace(strTmp, "${fld:colname_mark}", "("+values+")");
						
						TemplateEngine t = new TemplateEngine(getContext(), getRequest(), strTmp);
						t.replace(rsField, "");
						qFilter.append(StringUtil.replace(t.toString(), "${operator}", data_operator[0].trim()));
					}
				}else{
					if(data != null && (data_operator[0].equalsIgnoreCase("is not null") || data_operator[0].equalsIgnoreCase("is null"))){
						String strTmp = qClase;
						strTmp = StringUtil.replace(strTmp, "${table_code}", "1".equals(is_virtual_type)?"":table_code+".");
						strTmp = StringUtil.replace(strTmp, "${fld:colname_mark}", "");
						TemplateEngine t = new TemplateEngine(getContext(), getRequest(), strTmp);
						t.replace(rsField, "");
						qFilter.append(StringUtil.replace(t.toString(), "${operator}", ("llike".equalsIgnoreCase(data_operator[0])||"rlike".equalsIgnoreCase(data_operator[0]))?"like":data_operator[0]));
					}
				}
			}
		}
		
		rsTable.first();
		String report_sql = rsTable.getString("report_sql");
		
		if (!(report_sql == null || report_sql.equalsIgnoreCase(""))){
			report_sql = StringUtil.replace(report_sql, "${DEF", "${def");
			report_sql = StringUtil.replace(report_sql, "${FLD", "${fld");
			report_sql = StringUtil.replace(report_sql, "${SES", "${ses");
			report_sql = StringUtil.replace(report_sql, "${LBL", "${lbl");
		}

		report_sql = StringUtil.replace(report_sql, "${filter}", qFilter.toString());

		// 生成暂存变量值的结果集
		Recordset detail = new Recordset();
		rsField.top();
		while (rsField.next()) {
			field_alias = rsField.getString("field_code_alias");
			String type = rsField.getString("field_type");
			if(type.equalsIgnoreCase("integer"))
				detail.append(field_alias + "_filter", Types.INTEGER);
			else if (type.equalsIgnoreCase("double"))
				detail.append(field_alias + "_filter", Types.DOUBLE);
			else if (type.equalsIgnoreCase("date"))
				detail.append(field_alias + "_filter", Types.DATE);
			else if (type.equalsIgnoreCase("timestamp"))
				detail.append(field_alias + "_filter", Types.TIMESTAMP);
			else
				detail.append(field_alias + "_filter", Types.VARCHAR);
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

			if (data != null && data[0] != null && !data[0].trim().equals("")) {
				/* only accept single value parameters - not arrays */
				if (data.length == 1) {
					value = data[0].trim();
					String type = rsField.getString("field_type");
					
					if(type.equalsIgnoreCase("integer")){
						Integer d = ValidatorUtil.testInteger(value);
						detail.setValue(field_alias + "_filter", d);
					}
					else if (type.equalsIgnoreCase("double")){
						Double d = ValidatorUtil.testDouble(value);
						detail.setValue(field_alias + "_filter", d);
					}
					else if (type.equalsIgnoreCase("date")){
						Date d = ValidatorUtil.testDate(value, dateFormat);
						detail.setValue(field_alias + "_filter", d);
					}
					else if (type.equalsIgnoreCase("timestamp")){
						Date d = ValidatorUtil.testDate(value, datetimeFormat);
						detail.setValue(field_alias + "_filter", d);
					}
					else{
						if (data_operator[0].equalsIgnoreCase("like")) {
							value = "%" + value + "%";
						}else if(data_operator[0].equalsIgnoreCase("llike")){
							value = "%" + value;
						}else if(data_operator[0].equalsIgnoreCase("rlike")){
							value = value + "%";
						}
							
						detail.setValue(field_alias + "_filter", value);
					}
				}
			}

		}


		String sql = getSQL(report_sql, inputs);
		sql = getSQL(sql, detail);

		Recordset rs = getDb().get(sql);

		publish("datas",rs);
		
		String charts = getSQL(getResource("query_charts.sql"), inputs);
		Recordset chartsRs = getDb().get(charts);
		
		publish("query-base",chartsRs);
		
		return rc;
	}
	
	String buildGridArrayRecordset(Recordset rs) throws Throwable {
		String field = "";
		
		rs.top();
		while (rs.next()) {
			
			field = field + rs.getString("field_code") + " as " + rs.getString("colname") + ",";
			
		}
		
		if (field.endsWith(","))
			field = field.substring(0, field.length() - 1);

		return field;
	}

	String buildGroupByArrayRecordset(Recordset rs,String is_drill) throws Throwable {
		String field = "";
		
		rs.top();
		while (rs.next()) {
			if (field.equals(""))
				field = "group by ";

			
			field = field + rs.getString("field_code") + ",";
//			if(is_drill!=null && is_drill.equals("1"))
//				field = field + rs.getString("field_code_rawdata") + ",";
		}
		
		if (field.endsWith(","))
			field = field.substring(0, field.length() - 1);

		return field;
	}

	String buildOrderByArrayRecordset(Recordset rs) throws Throwable {
		String field = "";
		
		rs.top();
		while (rs.next()) {
			if (field.equals(""))
				field = "order by ";

			
			field = field + rs.getString("field_code") + ",";
		}
		
		if (field.endsWith(","))
			field = field.substring(0, field.length() - 1);

		return field;
	}
}
