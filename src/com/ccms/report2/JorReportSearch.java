package com.ccms.report2;

import java.sql.Types;
import java.util.regex.Pattern;

import dinamica.GenericTransaction;
import dinamica.Recordset;
import dinamica.RequestValidationException;
import dinamica.ValidatorUtil;

public class JorReportSearch extends GenericTransaction{

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
					case Types.TIMESTAMP:
						java.util.Date d = ValidatorUtil.testDate(value, sqlType == Types.DATE ? dateFormat : datetimeFormat);
						if (d == null)
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
			getRequest().setAttribute("dinamica.error.validation", "/action/subject/report/reportgen/jorsearch/validation");
			getRequest().setAttribute("dinamica.errors", errors.getErrors());
			throw errors;
		}
		
		return rc;
	}
}
