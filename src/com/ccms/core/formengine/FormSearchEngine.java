package com.ccms.core.formengine;

import java.sql.Types;
import java.util.Date;
import java.util.regex.Pattern;

import com.ccms.Constants;
import com.ccms.caches.CacheProvider;
import com.ccms.core.foctory.FormBean;
import com.ccms.core.foctory.FormFactory;
import com.ccms.dialect.Dialect;
import com.ccms.dialect.DialectFactory;

import dinamica.GenericTransaction;
import dinamica.Recordset;
import dinamica.RequestValidationException;
import dinamica.StringUtil;
import dinamica.TemplateEngine;
import dinamica.ValidatorUtil;
import dinamica.security.DinamicaUser;

public class FormSearchEngine extends GenericTransaction {

	public int service(Recordset inputs) throws Throwable {

		int rc = super.service(inputs);

		String claseFile = getConfig().getConfigValue("clase-template", "clause-field.sql");
		String pagingRecordsetName = getConfig().getConfigValue("paging-recordset", "query.sql");
		String totalRecordsetName = getConfig().getConfigValue("total-recordset", "query-total.sql");
		String formId = getConfig().getConfigValue("form-id", "form_id");
		
		//首先验证权限
		//从缓存中取数据
		CacheProvider cp = new FormProviderImpl();
		FormBean form = cp.getFormBeanById(inputs.getInteger(formId));
		DinamicaUser user = (DinamicaUser)getSession().getAttribute(Constants.DINAMICA_SECURITY_LOGIN);
		if(!FormFactory.hasPriviledge(form, user, Constants.PRIVILEDGE_SEARCH)){
			throw new Throwable(getSQL(Constants.NO_PERMISION, null));
		}
		
		//当前语言
		String locale = getSession().getAttribute(Constants.DINAMICA_USER_LOCALE).toString();
		
		String qClase = getResource(claseFile);

		StringBuffer qFilter = new StringBuffer();
		String field_alias = "";
		String value = "";
		
		Recordset rsField = form.getQueryFilterField().copy();
		
		
		RequestValidationException errors = new RequestValidationException();
		String dateFormat = "yyyy-MM-dd";
		String datetimeFormat = "yyyy-MM-dd HH:mm:ss";
		
		// 检测参数表中参数
		rsField.top();
		while (rsField.next()) {
			
			String field = "";
			String type = "";
			int sqlType = Types.VARCHAR;
			String label = null;
			String required = "";
			String regexp = null;
			String regexpError = null;

			field = rsField.getString("field_code_alias") + "_filter";
			type = rsField.getString("field_type");
			required = rsField.getString("is_required");
			if (required == null){
				required = "1";
			}

			if("en".equalsIgnoreCase(locale)){
				label = rsField.getString("field_name_en");
			}else{
				label = rsField.getString("field_name_cn");
			}
			
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

			if (data != null && data[0] != null && !data[0].trim().equalsIgnoreCase("")) {
				/* only accept single value parameters - not arrays */
				if (data.length == 1) {
					/* 校验数据 */
					value = data[0].trim();

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
					errors.addMessage(field, label + ": " + "${lbl:parameter_required}");
				}
			}
			
			field_alias = rsField.getString("field_code_alias");
			if (field_alias == null)
				break;

			String data_operator[] = getRequest().getParameterValues(field_alias + "_operator");
			if (data == null || data_operator == null) {
				continue;
			}

			//2 为 checkbox 多选
			String showType = rsField.getString("show_type");
			String fieldType = rsField.getString("field_type");

			if (data[0] != null && data_operator[0] != null && !data[0].trim().equals("")) {
				/* only accept single value parameters - not arrays */
				if (data.length == 1 && !data_operator[0].equalsIgnoreCase("in") && !data_operator[0].equalsIgnoreCase("not in") && !"2".equalsIgnoreCase(showType)) {
					// 如果为空或非空
					String strTmp = qClase;
					if (data_operator[0].equalsIgnoreCase("is not null") || data_operator[0].equalsIgnoreCase("is null")) {
						strTmp = StringUtil.replace(strTmp, "${fld:colname_mark}", "");
					}

					TemplateEngine t = new TemplateEngine(getContext(), getRequest(), strTmp);
					t.replace(rsField, "");
					qFilter.append(StringUtil.replace(t.toString(), "${operator}", ("llike".equalsIgnoreCase(data_operator[0])||"rlike".equalsIgnoreCase(data_operator[0]))?"like":data_operator[0]));
				}

				//单独处理checkbox 多选情况
				if (data.length > 0 && (data_operator[0].equalsIgnoreCase("in") || data_operator[0].equalsIgnoreCase("not in")) && "2".equalsIgnoreCase(showType)) {
					String strTmp = qClase;
					String values = "";
					for(int i=0;i<data.length;i++){
						values += (fieldType.equalsIgnoreCase("varchar")?"'":"")+data[i]+(fieldType.equalsIgnoreCase("varchar")?"'":"")+",";
					}
					values = values.substring(0,values.length()-1);
					strTmp = StringUtil.replace(strTmp, "${fld:colname_mark}", "("+values+")");
					
					TemplateEngine t = new TemplateEngine(getContext(), getRequest(), strTmp);
					t.replace(rsField, "");
					qFilter.append(StringUtil.replace(t.toString(), "${operator}", data_operator[0].trim()));
				}

			}else{
				if(data_operator[0].equalsIgnoreCase("is not null") || data_operator[0].equalsIgnoreCase("is null")){
					String strTmp = qClase;
					strTmp = StringUtil.replace(strTmp, "${fld:colname_mark}", "");
					TemplateEngine t = new TemplateEngine(getContext(), getRequest(), strTmp);
					t.replace(rsField, "");
					qFilter.append(StringUtil.replace(t.toString(), "${operator}", ("llike".equalsIgnoreCase(data_operator[0])||"rlike".equalsIgnoreCase(data_operator[0]))?"like":data_operator[0]));
				}
			}
		}

	
		/* 输出错误提示 */
		if (errors.getErrors().getRecordCount() > 0) {
			getRequest().setAttribute("dinamica.error.validation", "/action/ccms/validate");
			getRequest().setAttribute("dinamica.errors", errors.getErrors());
			throw errors;
		}
	
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
			if (field_alias == null)
				continue;

			String data[] = getRequest().getParameterValues(field_alias + "_filter");
			String data_operator[] = getRequest().getParameterValues(field_alias + "_operator");

			if(data != null && data[0] != null && !data[0].trim().equals("")){
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
		//拼装查询语句
		String qBase = null;
		
		if("en".equalsIgnoreCase(locale)){
			qBase = form.getSearch_sql_en();
		}else{
			qBase = form.getSearch_sql_cn();
		}
		qBase = StringUtil.replace(qBase, "${filter}", qFilter.toString());

		//取得排序字段
		String sort = getRequest().getParameter("sort");
		String order = getRequest().getParameter("order");
		if (sort != null && sort.length() > 0) {	//用户在界面手动指定了排序列
			String sortStr = "order by ";
			if(order == null){
				order = "";
			}
			sortStr = sortStr + sort + " " + order;
			qBase = StringUtil.replace(qBase, "${orderby}", sortStr);
		}else{
			if("en".equalsIgnoreCase(locale)){
				qBase = StringUtil.replace(qBase, "${orderby}", form.getSearch_orderby_en());
			}else{
				qBase = StringUtil.replace(qBase, "${orderby}", form.getSearch_orderby_cn());
			}
		}
		
		//数据限制读取
		String accessType = form.getAccess_type();
		String owerField = form.getOwner_field();
		String sqlAccess = "";
		if(owerField != null && owerField.length() > 0){
			if("0".equalsIgnoreCase(accessType) || "1".equalsIgnoreCase(accessType) || "2".equalsIgnoreCase(accessType) || "3".equalsIgnoreCase(accessType)){
				sqlAccess = getResource("query_access_data_"+accessType+".sql");
				sqlAccess = StringUtil.replace(sqlAccess, "${table}", form.getTable_code());
				sqlAccess = StringUtil.replace(sqlAccess, "${owner_field}", owerField);
			}
		}
		
		qBase = StringUtil.replace(qBase, "${access_type}", sqlAccess);
		
		String sql = getSQL(qBase, inputs);
		sql = getSQL(sql, detail);

		//查询总数
		String countSql = StringUtil.replace(form.getSearch_count_sql(), "${filter}", qFilter.toString());
		
		countSql = StringUtil.replace(countSql, "${access_type}", sqlAccess);
		
		countSql = getSQL(countSql, inputs);
		countSql = getSQL(countSql, detail);
		Recordset rsCount = getDb().get(countSql);
		rsCount.first();
		Integer record_count = rsCount.getInteger("record_count");
		
		//根据不同的数据库执行不同的分页
		String currentPage = (String)getRequest().getParameter("pageNo");
		Integer currPage = 1;
		Integer totalPage = 0;
		Integer pageSize = inputs.getString("__page_size__")==null?form.getPage_size():inputs.getInteger("__page_size__");
		if (currentPage!=null && !currentPage.equals("")) {
			currPage = Integer.parseInt(currentPage);
			if(currPage == 0) currPage=1;
		}
		totalPage = ((record_count-1)/pageSize)+1;
		
		Recordset rsPage = new Recordset();
		rsPage.append("total", Types.INTEGER);
		rsPage.append("pageno", Types.INTEGER);
		rsPage.append("pages", Types.INTEGER);
		rsPage.addNew();
		rsPage.setValue("total", record_count);
		rsPage.setValue("pages", totalPage);
		rsPage.setValue("pageno", currPage);
		
		//将查询语句放到session，方便导出时直接使用
		getRequest().getSession().setAttribute("QuerySQL_"+inputs.getInteger(formId), sql);
		
		Recordset rs = null;
		//如果总记录数为0，则不需要再查一遍
		if(record_count > 0){
			Dialect dialect = DialectFactory.buildDialect(getConnection().getMetaData().getDatabaseProductName().toLowerCase());
			sql = dialect.getLimitString(sql, pageSize*(currPage-1), pageSize);
			rs = getDb().get(sql);
		}else{
			rs = new Recordset();
		}
		publish(pagingRecordsetName, rs);
		publish(totalRecordsetName, rsPage);
		return rc;
	}
}
