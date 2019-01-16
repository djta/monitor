package com.ccms.core.formgen;

import java.sql.Types;
import java.util.regex.Pattern;

import com.ccms.Constants;
import com.ccms.caches.CacheProvider;
import com.ccms.caches.impl.CacheProviderImpl;
import com.ccms.core.foctory.FormBean;
import com.ccms.core.foctory.FormFactory;

import dinamica.Db;
import dinamica.GenericTableManager;
import dinamica.GenericTransaction;
import dinamica.IServiceWrapper;
import dinamica.Recordset;
import dinamica.RequestValidationException;
import dinamica.StringUtil;
import dinamica.ValidatorUtil;
import dinamica.security.DinamicaUser;

public class FormUpdateGen extends GenericTableManager implements IServiceWrapper {
	
	FormBean form = null;//查询校验类、替换类和后处理类
	@Override
	public int service(Recordset inputs) throws Throwable {
		Integer form_id = inputs.getInteger("form_id");
		//每次都从缓存中重新复制一份
		CacheProvider cp = new CacheProviderImpl();
		form = cp.getFormBeanById(form_id);
		//首先验证权限
		DinamicaUser user = (DinamicaUser)getSession().getAttribute(Constants.DINAMICA_SECURITY_LOGIN);
		if(!FormFactory.hasPriviledge(form, user, Constants.PRIVILEDGE_UPDATE)){
			throw new Throwable(getSQL(Constants.NO_PERMISION, null));
		}
		
		int rc = super.service(inputs);

		/* to store validation error messages */
		RequestValidationException errors = new RequestValidationException();
		String dateFormat = "yyyy-MM-dd";
		String datetimeFormat = "yyyy-MM-dd HH:mm:ss";

		Db db = getDb();

		Recordset rsField = form.getUpdateQueryField().copy();
		
		Recordset inputParams = inputs.copyStructure();

		rsField.top();
		while (rsField.next()) {
			String field = "";
			String type = "";
			int sqlType = 0;

			field = rsField.getString("field_code");
			type = rsField.getString("field_type");

			/* validate type attribute */
			if (type.equalsIgnoreCase("varchar"))
				sqlType = Types.VARCHAR;
			else if (type.equalsIgnoreCase("integer"))
				sqlType = Types.INTEGER;
			else if (type.equalsIgnoreCase("double"))
				sqlType = Types.DOUBLE;
			else if (type.equalsIgnoreCase("date"))
				sqlType = Types.DATE;
			else if (type.equalsIgnoreCase("timestamp"))
				sqlType = Types.TIMESTAMP;
			else
				throw new Exception("Invalid validator data type (" + type + ")");

			/* OK - append the field to the recordset */
			inputParams.append(field, sqlType);
		}
		inputParams.addNew();
		inputs.copyValues(inputParams);
		rsField.top();
		while (rsField.next()) {
			String field = "";
			String value = "";
			String type = "";
			int sqlType = 0;
			String label = null;
			String required = "";
			int maxLen = 0;
			String regexp = null;
			String regexpError = null;

			field = rsField.getString("field_code");
			type = rsField.getString("field_type");
			required = rsField.getString("is_required");
			if (required == null)
				required = "1";

			String field_name_lable = getSQL("field_name_${def:locale}",null);

			label = rsField.getString(field_name_lable);
			if (rsField.getString("field_length") != null)
				maxLen = Integer.parseInt(rsField.getString("field_length"));

			regexp = rsField.getString("format_mark");
			regexpError = "输入格式错误:" + regexp;

			/* validate type attribute */
			if (type.equalsIgnoreCase("varchar"))
				sqlType = Types.VARCHAR;
			else if (type.equalsIgnoreCase("integer"))
				sqlType = Types.INTEGER;
			else if (type.equalsIgnoreCase("double"))
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
						if (value.length() > maxLen)
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
						else
							inputParams.setValue(field, d);

						break;

					case Types.DOUBLE:
						Double dbl = ValidatorUtil.testDouble(value);
						if (dbl == null)
							errors.addMessage(field, label + ": " + "${lbl:invalid_double}");
						else
							inputParams.setValue(field, dbl);

						break;

					case Types.INTEGER:
						Integer i = ValidatorUtil.testInteger(value);
						if (i == null)
							errors.addMessage(field, label + ": " + "${lbl:invalid_integer}");
						else
							inputParams.setValue(field, i);

						break;

					case Types.VARCHAR:
						inputParams.setValue(field, value);
						break;

					}

				}
			} else {
				if (required.equalsIgnoreCase("0")) /* 非空检测 */
				{
					errors.addMessage(field, rsField.getString(field_name_lable) + ": " + "${lbl:parameter_required}");
				}
			}
		}

		/* 输出错误提示 */
		if (errors.getErrors().getRecordCount() > 0) {
			getRequest().setAttribute("dinamica.error.validation", "/action/error/validation/ajax");
			getRequest().setAttribute("dinamica.errors", errors.getErrors());
			throw errors;
		}

		//替换类处理
		boolean is_user_replace_class = false;
        String validatorClassName = form.getUpdate_classname_validator();
        if(validatorClassName!=null && validatorClassName.length() > 0){
    	    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    		GenericTransaction t = (GenericTransaction) loader.loadClass(validatorClassName).newInstance();
			t.init(this.getContext(), this.getRequest(), this.getResponse());
			t.setConfig(this.getConfig());
			t.setConnection(this.getConnection());
            t.service(inputParams);
	    }
        
        String replaceClassName = form.getUpdate_classname_replace();
	    if(replaceClassName!=null && replaceClassName.length() > 0){
    	    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    		GenericTransaction t = (GenericTransaction) loader.loadClass(replaceClassName).newInstance();
			t.init(this.getContext(), this.getRequest(), this.getResponse());
			t.setConfig(this.getConfig());
			t.setConnection(this.getConnection());
            t.service(inputParams);
            is_user_replace_class = true;
	    }
		
		if(is_user_replace_class == false){
			//控制事务
			db.beginTrans();
			
			String update = getSQL(form.getUpdate_sql(), inputParams);
	
			db.exec(update);
			
			//记录审计数据
			String updateAudit = form.getUpdate_audit_sql();
			updateAudit = StringUtil.replace(updateAudit, "${pk_value}", inputs.getString("__pk_value__"));
			db.exec(getSQL(updateAudit, null));
	
			//取出当前表的快照
			Recordset rsSnapshot = db.get(getSQL(form.getQuery_snapshot_sql(), inputParams));
			
			// 判断字段是否修改
			String strInsertTableDataLogSql = getResource("insert_table_data_log.sql");
			strInsertTableDataLogSql = StringUtil.replace(strInsertTableDataLogSql, "${fld:incident_code}", "''");
			strInsertTableDataLogSql = StringUtil.replace(strInsertTableDataLogSql, "${table_code}", form.getTable_code());
			strInsertTableDataLogSql = StringUtil.replace(strInsertTableDataLogSql, "${pk_value}", inputParams.getString("__pk_value__"));
			strInsertTableDataLogSql = StringUtil.replace(strInsertTableDataLogSql, "${form_id}", form.getForm_id().toString());
			//替换快照版本号
			rsSnapshot.first();
			strInsertTableDataLogSql = getSQL(strInsertTableDataLogSql, rsSnapshot);

			rsField.top();
			while (rsField.next()) {
				if (rsField.getString("field_code").equalsIgnoreCase(form.getBpk_field()))
					continue;
				String strTmp = getTableFieldDiffValue(rsField.getString("field_code"), getSQL(strInsertTableDataLogSql, inputParams));
				if (!strTmp.equalsIgnoreCase(""))
					db.addBatchCommand(strTmp);
			}
			db.exec();
			
			//事务提交
			db.commit();
		}
		getRequest().setAttribute("inputParams", inputParams);
		publish("_request", inputParams);

		//将参数传过来
		String curdParamsId = getConfig().getConfigValue("crud-params-id","crud_params");
		String suffix = getConfig().getConfigValue("suffix-col","form_id");
		String outputParams = getConfig().getConfigValue("output-params-id","formgen.filter.params");
		
		String suffixValue = inputParams.getString(suffix);
		Recordset params = (Recordset)getRequest().getSession().getAttribute(curdParamsId+"_"+suffixValue);
		
		publish(outputParams, params);

		return rc;
	}

	public void afterService(Recordset inputs) throws Throwable {

		Recordset inputParams = (Recordset) getRequest().getAttribute("inputParams");

		Recordset rsForm = getRecordset("query-form.sql");
		getRequest().setAttribute("query-form.sql", rsForm);
		
		String strClassName = form.getUpdate_classname();
		if (strClassName == null || strClassName.equalsIgnoreCase(""))
			return;

		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		GenericTransaction t = (GenericTransaction) loader.loadClass(strClassName).newInstance();
		
		t.init(this.getContext(), this.getRequest(), this.getResponse());
		t.setConfig(this.getConfig());
		t.setConnection(this.getConnection());
		t.service(inputParams);

		String strClassName1 = form.getUpdate_classname1();
		if (strClassName1 == null || strClassName1.equalsIgnoreCase(""))
			return;

		ClassLoader loader1 = Thread.currentThread().getContextClassLoader();
		GenericTransaction t1 = (GenericTransaction) loader1.loadClass(strClassName1).newInstance();
		
		t1.init(this.getContext(), this.getRequest(), this.getResponse());
		t1.setConfig(this.getConfig());
		t1.setConnection(this.getConnection());
		t1.service(inputParams);
	}

	public void beforeService(Recordset inputParams) throws Throwable {
	}

	String getTableFieldDiffValue(String field_code, String sql) throws Throwable {
		if (getRequest().getParameterValues("_" + field_code + "_") == null)
			return "";
		if (getRequest().getParameterValues(field_code) == null)
			return "";

		String strBeforeValue = getRequest().getParameterValues("_" + field_code + "_")[0].trim();
		strBeforeValue = StringUtil.replace(strBeforeValue, "'", "''");
		String strAfterValue = getRequest().getParameterValues(field_code)[0].trim();
		strAfterValue = StringUtil.replace(strAfterValue, "'", "''");
		String sql_return = sql;

		if (!strAfterValue.equals(strBeforeValue)) {
			sql_return = StringUtil.replace(sql_return, "${field_code}", field_code);
			//最多支持4000长度
		    sql_return = StringUtil.replace(sql_return, "${before_value}", strBeforeValue.substring(strBeforeValue.length()>4000?strBeforeValue.length()-4000:0));
		    sql_return = StringUtil.replace(sql_return, "${after_value}", strAfterValue.substring(strAfterValue.length()>4000?strAfterValue.length()-4000:0));
		} else {
			sql_return = "";
		}
		return sql_return;
	}

}
