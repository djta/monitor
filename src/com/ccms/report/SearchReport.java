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

		// reutiliza la logica de la clase padre
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
		String rowheadRecordsetName = getConfig().getConfigValue("rowhead-recordset", "query_rowhead_field.sql");
		String colheadRecordsetName = getConfig().getConfigValue("colhead-recordset", "query_colhead_field.sql");
		
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
		String is_drill = rsReport.getString("is_drill");//是否钻取
		
		qfieldGrid = getSQL(qfieldGrid, rsReport);		//replace domain_value_show_type
		Recordset rsFieldGrid = getDb().get(qfieldGrid);

		String qtable = getSQL(getResource("query_table.sql"), inputs);
		Recordset rsTable = getDb().get(qtable);
		rsTable.first();
		String table_code = rsTable.getString("table_code");

		String qgroupby = getSQL(getResource("query_group_by.sql"), inputs);
		Recordset rsGroupBy = getDb().get(qgroupby);


		

		// 检测字段合法性
		rsField.top();
		while (rsField.next()) {
			String field_alias = "";
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

			field_alias = rsField.getString("field_code_alias");
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
			if (type.equalsIgnoreCase("integer"))
				sqlType = Types.INTEGER;
			else if (type.equalsIgnoreCase("double"))
				sqlType = Types.DOUBLE;
			else if (type.equalsIgnoreCase("date"))
				sqlType = Types.DATE;
			else if (type.equalsIgnoreCase("timestamp"))
				sqlType = Types.TIMESTAMP;
			else
				sqlType = Types.VARCHAR;

			String data[] = getRequest().getParameterValues(field);
			String data_operator[] = getRequest().getParameterValues(field_alias + "_operator");
			
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
						if (i == null && !(data_operator[0].equalsIgnoreCase("in") || data_operator[0].equalsIgnoreCase("not in")))
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
			getRequest().setAttribute("dinamica.error.validation", "/action/error/validation/ajax");
			getRequest().setAttribute("dinamica.errors", errors.getErrors());
			throw errors;
		}

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
		
		//设置字段顺序
		String qorderby = getSQL(getResource("query_order_by_field.sql"), inputs);
		Recordset rsOrderBy = getDb().get(qorderby);
		
		Recordset filter = new Recordset();
		filter.append("filter", Types.VARCHAR);
		filter.append("__auto_search__", Types.VARCHAR);
		filter.addNew();
		filter.setValue("filter", sbFilter.toString());
		filter.setValue("__auto_search__", "");
		
/*		String rowheadRecordsetSql = getSQL(getSQL(getResource(rowheadRecordsetName), inputs),filter);
		Recordset rsAixsX = getDb().get(rowheadRecordsetSql);
		
		String colheadRecordsetSql = getSQL(getSQL(getResource(colheadRecordsetName), inputs),filter);
		Recordset rsAixsY = getDb().get(colheadRecordsetSql);
		
		publish(rowheadRecordsetName,rsAixsX);
		publish(colheadRecordsetName,rsAixsY);
*/
		Recordset rsAixsX = getRecordset("query_rowhead_field.sql");
		Recordset rsAixsY = getRecordset("query_colhead_field.sql");

		rsOrderBy.top();
		while(rsOrderBy.next()){
			rsAixsX.top();
			while(rsAixsX.next()){
				if(rsAixsX.getString("field_report").equals(rsOrderBy.getString("field_report"))){
					rsOrderBy.setValue("show_order", new Integer(rsAixsX.getString("show_order")==null?"0":rsAixsX.getString("show_order")));
				}
			}
			rsAixsY.top();
			while(rsAixsY.next()){
				if(rsAixsY.getString("field_report").equals(rsOrderBy.getString("field_report"))){
					rsOrderBy.setValue("show_order", new Integer(rsAixsY.getString("show_order")==null?"0":rsAixsY.getString("show_order")));
				}
			}
		}
		rsOrderBy.sort("show_order");
		
		rsTable.first();
		String table = rsTable.getString("table_code");
		String delete_field = rsTable.getString("delete_field");
		String report_sql = rsTable.getString("report_sql");
		String fields = buildGridArrayRecordset(rsFieldGrid,is_drill);
		String group_by = buildGroupByArrayRecordset(rsGroupBy,is_drill);
		String order_by = buildOrderByArrayRecordset(rsOrderBy);

		if (!(report_sql == null || report_sql.equalsIgnoreCase(""))){
			report_sql = StringUtil.replace(report_sql, "${DEF", "${def");
			report_sql = StringUtil.replace(report_sql, "${FLD", "${fld");
			report_sql = StringUtil.replace(report_sql, "${SES", "${ses");
			report_sql = StringUtil.replace(report_sql, "${LBL", "${lbl");
			qBase = StringUtil.replace(qBase, "${table}", report_sql);
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

		// ahora reemplaza los valores de los parametros en el query
		String sql = getSQL(qBase, inputs);
		sql = getSQL(sql, detail);

		// ejecutar query y crear recordset
		Recordset rs = getDb().get(sql);

		
		getSession().setAttribute(rowheadRecordsetName, rsAixsX);
		getSession().setAttribute(colheadRecordsetName, rsAixsY);
		getSession().setAttribute("query_report.sql", rsReport);
		if (rs.getRecordCount() > 0) {
			getSession().setAttribute(pagingRecordsetName, rs);;
			publish(pagingRecordsetName, rs);
			rc = 0;
		} else {
			// rs.addNew(); /*add a blank row*/
			getSession().setAttribute(pagingRecordsetName, rs);;
			publish(pagingRecordsetName, rs);
			
			rc = 1;
		}

		return rc;

	}

	String buildGridArrayRecordset(Recordset rs,String is_drill) throws Throwable {
		String field = "";
		// recorrer recordset
		rs.top();
		while (rs.next()) {
			// asignar nombre del campo junto con la coma
			field = field + rs.getString("field_code") + " as " + rs.getString("colname") + ",";
			if(is_drill!=null && is_drill.equals("1")){
				if(rs.getString("is_group_by").equals("1")){	//为分组列
					field = field + rs.getString("field_code_rawdata") + " as " + rs.getString("colname_rawdata") + ",";
				}
			}
		}
		// ultimo valor quitarle la coma
		if (field.endsWith(","))
			field = field.substring(0, field.length() - 1);

		return field;
	}

	String buildGroupByArrayRecordset(Recordset rs,String is_drill) throws Throwable {
		String field = "";
		// recorrer recordset
		rs.top();
		while (rs.next()) {
			if (field.equals(""))
				field = "group by ";

			// asignar nombre del campo junto con la coma
			field = field + rs.getString("field_code") + ",";
//			if(is_drill!=null && is_drill.equals("1"))
//				field = field + rs.getString("field_code_rawdata") + ",";
		}
		// ultimo valor quitarle la coma
		if (field.endsWith(","))
			field = field.substring(0, field.length() - 1);

		return field;
	}

	String buildOrderByArrayRecordset(Recordset rs) throws Throwable {
		String field = "";
		// recorrer recordset
		rs.top();
		while (rs.next()) {
			if (field.equals(""))
				field = "order by ";

			// asignar nombre del campo junto con la coma
			field = field + rs.getString("field_code") + ",";
		}
		// ultimo valor quitarle la coma
		if (field.endsWith(","))
			field = field.substring(0, field.length() - 1);

		return field;
	}

}
