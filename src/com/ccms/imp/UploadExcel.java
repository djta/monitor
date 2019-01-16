package com.ccms.imp;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dinamica.Config;
import dinamica.GenericTransaction;
import dinamica.Recordset;
import dinamica.StringUtil;
import dinamica.ValidatorUtil;

public class UploadExcel extends GenericTransaction  {

	private Map<String, Map<String, String>> domainMap = null;//查询出参数表
	private Map<String, Map<String, String>> mappingMap = null;//查询出错误信息对照表

	@Override
	public int service(Recordset inputParams) throws Throwable {
		
		int rc = super.service(inputParams);
		
		if (inputParams.isNull("file.filename"))
			throw new Throwable(ExcelImportUtil.ERROR_FILE_NULL);

		String file = inputParams.getString("file");
		String fileName = (String) inputParams.getValue("file.filename");
		fileName = fileName.substring(fileName.lastIndexOf(File.separator) + 1);
		fileName = formatRequestEncoding(fileName);
		inputParams.setValue("file.filename", fileName);
		
		String extension = fileName.lastIndexOf(".") == -1 ? "" : fileName.substring(fileName.lastIndexOf(".") + 1);
		File f = new File(file);
		//数据处理成list
		ExcelImportUtil excel;
		if ("xls".equals(extension)) {
			if(f.length() > ExcelImportUtil.MAX_LENGTH_2K3){
				throw new Throwable(ExcelImportUtil.ERROR_MAX_SIZE_ILLEGAL);
			}
			excel = new ExcelImportFor2k3(f);
		} else if ("xlsx".equals(extension)) {
			excel = new ExcelImportFor2k7(f);
		} else {
			throw new Throwable(ExcelImportUtil.ERROR_FORMAT_ILLEGAL);
		}
		f.delete();
		
		List<List<String>> dataList = excel.getExcelData();
		if(dataList.size() == 0){
			throw new Throwable(ExcelImportUtil.ERROR_FILE_EMPTY);
		}
		
		Recordset importTitle = getRecordset("query-import.sql");
		Recordset importTable = getRecordset("query-import_table.sql");
		Recordset importTableField = getRecordset("query-import_field.sql");
		
		//检验标题列是否在配置中存在
		importTableField.top();
		Map<String, Map<Integer,FieldBean>> fieldMap = new HashMap<String, Map<Integer,FieldBean>>();
		while(importTableField.next()){
			Integer tab_id = importTableField.getInteger("tab_id");
			String col_name = importTableField.getString("col_name");
			if(fieldMap.containsKey(col_name)){
				Map<Integer, FieldBean> beanMap = fieldMap.get(col_name);
				beanMap.put(tab_id, FieldBean.transformField(importTableField));
			}else{
				Map<Integer, FieldBean> beanMap = new HashMap<Integer, FieldBean>();
				beanMap.put(tab_id, FieldBean.transformField(importTableField));
				fieldMap.put(col_name, beanMap);
			}
		}
		
		Recordset rsTitle = new Recordset();
		rsTitle.append( "col_code", java.sql.Types.VARCHAR);
		rsTitle.append( "col_name", java.sql.Types.VARCHAR);
		rsTitle.append( "col_fld", java.sql.Types.VARCHAR);
		rsTitle.append( "show_align", java.sql.Types.VARCHAR);
		//存储标题行名称以防重复
		Map<String, String> titleMap = new HashMap<String, String>();
		//反转存储标题序号与名称对应关系
		Map<String, String> titleMapRevise = new HashMap<String, String>();
		//第一行为定义字段
		
		//大数据池
		Recordset rs = new Recordset();
		
		importTitle.first();
		Integer title_line_num = (Integer) keyIsNull(importTitle, "title_line_num");
		//importTitle.isNull("title_line_num")?null : importTitle.getInteger("title_line_num");
		if(title_line_num > 0) title_line_num --;//读取excel时标题行从0开始。
		List<String> titleRow = dataList.get(title_line_num);
		int columnas = titleRow.size();
		for(int i = 0; i<columnas; i++)
		{
			String content = titleRow.get(i);
			if(content == null || content.length() == 0){
				throw new Throwable(String.format(ExcelImportUtil.ERROR_TITLE_NULL, (i+1)));
			}
			if(titleMap.containsKey(content)){
				throw new Throwable(String.format(ExcelImportUtil.ERROR_TITLE_REPEAT, content));
			}
			
			//设置字段类型 加 _code 存储经过转化的代码
			rs.append("col"+i, java.sql.Types.VARCHAR);
			
			rsTitle.addNew();
			rsTitle.setValue("col_code", "col"+i);
			rsTitle.setValue("col_fld", "${fld:col"+i+"@js}");
			
			if(fieldMap.containsKey(content)){
				Map<Integer, FieldBean> map = fieldMap.get(content);
				Iterator<Integer> keyIt = map.keySet().iterator();
				while(keyIt.hasNext()){
					FieldBean bean = map.get(keyIt.next());
					String field_type = bean.getField_type();
					//只有保存代码值时才会增加该字段，以防多个字段对应一个excel值时被后面的替换掉
					if("1".equals(bean.getIs_save_code())){
						if("date".equalsIgnoreCase(field_type)){
							rs.append("col"+i+"_code", java.sql.Types.DATE);
						}else if("timestamp".equalsIgnoreCase(field_type)){
							rs.append("col"+i+"_code", java.sql.Types.TIMESTAMP);
						}else if("integer".equalsIgnoreCase(field_type) || "int4".equalsIgnoreCase(field_type)){
							rs.append("col"+i+"_code", java.sql.Types.INTEGER);
						}else if("double".equalsIgnoreCase(field_type) || "numeric".equalsIgnoreCase(field_type)){
							rs.append("col"+i+"_code", java.sql.Types.DOUBLE);
						}else{
							rs.append("col"+i+"_code", java.sql.Types.VARCHAR);
						}
					}
					rsTitle.setValue("show_align", bean.getShow_align());
				}
				
				rsTitle.setValue("col_name", content);
			}else{
				rs.append("col"+i, java.sql.Types.VARCHAR);
				
				rsTitle.setValue("col_name", content+ExcelImportUtil.INFO_NO_MAPPING);
				rsTitle.setValue("show_align", "center");
			}
			
			titleMap.put(content, "col"+i);
			titleMapRevise.put("col"+i, content);
		}
		rsTitle.addNew();
		rsTitle.setValue("col_code", "error");
		rsTitle.setValue("col_fld", "${fld:error}");
		rsTitle.setValue("col_name", "错误信息");
		
		rs.append("error", java.sql.Types.VARCHAR);
		
		//验证主键别名是否有重复
		Map<String, String> bpkMap = new HashMap<String, String>();
		
		//拼各个表的业务主键值
		importTable.top();
		while(importTable.next()){
			String bpk_field_alias = importTable.getString("bpk_field_alias");
			String bpk_field_type = importTable.getString("bpk_field_type");
			
			if(bpk_field_alias == null || bpk_field_alias.length() <= 0){
				continue;
			}
			
			if(bpkMap.containsKey(bpk_field_alias)){
				throw new Throwable(String.format(ExcelImportUtil.ERROR_PK_REPEAT,bpk_field_alias));
			}
			if(titleMap.containsKey(bpk_field_alias)){
				throw new Throwable(String.format(ExcelImportUtil.ERROR_PK_EXISTS,bpk_field_alias));
			}
			
			bpkMap.put(bpk_field_alias, bpk_field_alias);
			titleMap.put(bpk_field_alias, bpk_field_alias);
			
			rs.append(bpk_field_alias, java.sql.Types.VARCHAR);
			if(bpk_field_type != null && "integer".equalsIgnoreCase(bpk_field_type)){
				rs.append(bpk_field_alias+"_code", java.sql.Types.INTEGER);
			}else{
				rs.append(bpk_field_alias+"_code", java.sql.Types.VARCHAR);
			}
		}
		
		
		//初始化数据，避免频繁访问数据库 begin
		//查询所有参数数据
		Recordset domainRs = getRecordset("query_all_domain.sql");
		domainMap = new HashMap<String, Map<String, String>>();
		domainRs.top();
		while(domainRs.next()){
			String domain_text = domainRs.getString("domain_text");
			String namespace = domainRs.getString("namespace");
			String domain_value = domainRs.getString("domain_value");
			if(domainMap.containsKey(namespace)){
				Map<String, String> domain = domainMap.get(namespace);
				domain.put(domain_text, domain_value);
			}else{
				Map<String, String> domain = new HashMap<String, String>();
				domain.put(domain_text, domain_value);
				domainMap.put(namespace, domain);
			}
		}
		//查询所有错误对应信息表
		Recordset mappingRs = getRecordset("query_all_mapping.sql");
		mappingMap = new HashMap<String, Map<String, String>>();
		mappingRs.top();
		while(mappingRs.next()){
			String mapping_value = mappingRs.getString("mapping_value");
			String namespace = mappingRs.getString("namespace_mapping");
			String old_data = mappingRs.getString("old_data");
			if(mappingMap.containsKey(namespace)){
				Map<String, String> mapping = mappingMap.get(namespace);
				mapping.put(old_data, mapping_value);
			}else{
				Map<String, String> mapping = new HashMap<String, String>();
				mapping.put(old_data, mapping_value);
				mappingMap.put(namespace, mapping);
			}
		}
		//初始化数据，避免频繁访问数据库 end
		
		//获取外键关联查询语句
		String fkSQL = getSQL(getResource("query_table.sql"),inputParams);
		
		//校验类
		String validator_class_name = (String) keyIsNull(importTitle, "validator_class_name");
		//importTitle.isNull("validator_class_name")?null : importTitle.getString("validator_class_name");
		if(validator_class_name != null && validator_class_name.length() > 0){
			getRequest().setAttribute("titleMap", titleMap);//放到request里面以供调用
		}
		StringBuffer errorSB = new StringBuffer();
		boolean if_has_error = false;
		String strColName = null;
		//读取excel数据
		for(int j = (title_line_num+1) ; j<dataList.size(); j++)
		{
			rs.addNew();
			errorSB.delete(0, errorSB.length());
			strColName = null;
			List<String> dataRow = dataList.get(j);
			for(int i = 0 ; i<dataRow.size(); i++)
			{
				try{
					String content = dataRow.get(i);
					rs.setValue("col"+i, content);
					
					strColName = titleMapRevise.get("col"+i);
					if(fieldMap.containsKey(strColName)){
						Map<Integer, FieldBean> map = fieldMap.get(strColName);
						Iterator<Integer> keyIt = map.keySet().iterator();
						while(keyIt.hasNext()){
							FieldBean bean = map.get(keyIt.next());
							//判断字段不能为空
							if(content == null || content.length() == 0){
								if("1".endsWith(bean.getIs_mandatory())){
									throw new Throwable(ExcelImportUtil.ERROR_NOT_NULL);
								}else{
									continue;
								}
							}
							if(bean.getField_length() > 0 && content.length() > bean.getField_length()){
								throw new Throwable(String.format(ExcelImportUtil.ERROR_LENGTH_OUT,bean.getField_length()));
							}
							String field_type = bean.getField_type();
							if("date".equalsIgnoreCase(field_type)){
								Date dcolum1 = ValidatorUtil.testDate(content, "yyyy-MM-dd");
								if (dcolum1 == null) {
									throw new Throwable(ExcelImportUtil.ERROR_DATE_ILLEGAL);
								}
								rs.setValue("col"+i+"_code", dcolum1);
							}else if("timestamp".equalsIgnoreCase(field_type)){
								Date dcolum1 = ValidatorUtil.testDate(content, "yyyy-MM-dd HH:mm:ss");
								if (dcolum1 == null) {
									throw new Throwable(ExcelImportUtil.ERROR_DATETIME_ILLEGAL);
								}
								rs.setValue("col"+i+"_code", dcolum1);
							}else if("integer".equalsIgnoreCase(field_type)){
								Integer dcolum1 = ValidatorUtil.testInteger(content);
								if (dcolum1 == null) {
									throw new Throwable(ExcelImportUtil.ERROR_INTEGER_ILLEGAL);
								}
								rs.setValue("col"+i+"_code", dcolum1);
							}else if("double".equalsIgnoreCase(field_type)){
								Double dcolum1 = ValidatorUtil.testDouble(content.replace(",", "."));
								if (dcolum1 == null) {
									throw new Throwable(ExcelImportUtil.ERROR_DOUBLE_ILLEGAL);
								}
								rs.setValue("col"+i+"_code", dcolum1);
							}else{//varchar
								//匹配namespace，匹配不上抛出错误，匹配上则给code字段赋值
								String namespace = bean.getDomain_namespace();
								if(namespace != null && namespace.length() > 0){
									String code = getNameSpaceValue(namespace, content);
									rs.setValue("col"+i+"_code", code);
								}else{
									//匹配外键表值
									String fk_schema = bean.getFk_schema();
									String fk_tab = bean.getFk_tab();
									String fk_fld_code = bean.getFk_fld_code();
									String fk_fld_name = bean.getFk_fld_name();
									if(fk_schema != null && fk_tab != null && fk_fld_code != null && fk_fld_name != null){
										String code = getFKValue(bean,content,fkSQL);
										rs.setValue("col"+i+"_code", code);
									}else{
										//根据正则表达式校验
										String regex_rule = bean.getRegex_rule();
										if(regex_rule != null && regex_rule.length() > 0){
											if (!regex(regex_rule, content)) {
												throw new Throwable(bean.getRegex_tip());
											}
										}
										//只有保存代码值时才会给该字段赋值，以防多个字段对应一个excel值时被后面的替换掉
										if("1".equals(bean.getIs_save_code())){
											rs.setValue("col"+i+"_code", encodeJS(content));
										}
									}
								}
							}
						}
					}
				}catch(Throwable b){
					if(strColName != null && strColName.length() > 0){
						errorSB.append(strColName).append(":").append(b.getMessage()).append(";");
					}else{
						errorSB.append(b.getMessage());
					}
					if_has_error = true;
				}
			}
			//执行校验类（针对复杂级联关系校验等）
			if(validator_class_name != null && validator_class_name.length() > 0){
				try{
					ClassLoader loader = Thread.currentThread().getContextClassLoader();
					GenericTransaction t = (GenericTransaction) loader.loadClass(validator_class_name).newInstance();
								
					t.init(this.getContext(), this.getRequest(), this.getResponse());
					t.setConfig(this.getConfig());
					t.setConnection(this.getConnection());
					t.service(rs);
				}catch(Throwable b){
					errorSB.append(b.getMessage()).append(";");
					if_has_error = true;
				}
			}
			rs.setValue("error", errorSB.toString());
		}
		if(if_has_error){
			rs.sort("error");
			rs.sort("error");
		}
		
		if(rs.getRecordCount() == 0){
			throw new Throwable(ExcelImportUtil.ERROR_FILE_EMPTY);
		}
		
		getRequest().getSession().setAttribute("rsTitle", rsTitle);
		getRequest().getSession().setAttribute("rsData", rs);
		getRequest().getSession().setAttribute("titleMap", titleMap);
		publish("rsTitle", rsTitle);
		publish("rsData", rs);
		
		//即使有错误也显示导入按钮
		String is_error_continue = (String) keyIsNull(importTitle, "is_error_continue");
				//(importTitle.containsField("is_error_continue") && !importTitle.isNull("is_error_continue")) ? importTitle.getString("is_error_continue") : null;
		if(is_error_continue != null && "1".equals(is_error_continue)){
			if(if_has_error == true){
				if_has_error = false;
			}
		}
		getRequest().getSession().setAttribute("if_show_import", if_has_error==true?"none":"inline");
		
		return rc;
	}
	
	protected String getNameSpaceValue(String strNameSpace, String strShowValue) throws Throwable {
		
		String oldShow = strShowValue;
		//加入参数表处理程序
		if("Province".equalsIgnoreCase(strNameSpace)){
			if(strShowValue.charAt(strShowValue.length()-1) == '省'){
				strShowValue = strShowValue.substring(0, strShowValue.length()-1);
			}
		}else if("Gender".equalsIgnoreCase(strNameSpace)){
			if("先生".equals(strShowValue) || "男".equals(strShowValue) || "Male 男".equals(strShowValue) || "阁下".equals(strShowValue) || "Male 男".equals(strShowValue)){
				return "0";
			}else if("小姐".equals(strShowValue) || "女".equals(strShowValue) || "女士".equals(strShowValue) || "夫人".equals(strShowValue) || "Female 女".equals(strShowValue)){
				return "1";
			}else{
				return "0";
			}
		}

		if(domainMap != null && domainMap.containsKey(strNameSpace)){
			Map<String, String> domain = domainMap.get(strNameSpace);
			if(domain.containsKey(strShowValue)){
				return domain.get(strShowValue);
			}else if(domain.containsKey(oldShow)){
				return domain.get(oldShow);
			}
		}
		//未匹配到，从错误字段数据映射表中查找
		if(mappingMap.containsKey(strNameSpace)){
			Map<String, String> mapping = mappingMap.get(strNameSpace);
			if(mapping.containsKey(strShowValue)){
				return mapping.get(strShowValue);
			}else if(mapping.containsKey(oldShow)){
				return mapping.get(oldShow);
			}
		}
		throw new Throwable(String.format(ExcelImportUtil.ERROR_NAMESPACE_ILLEGAL, oldShow));
	}
	
	protected String getFKValue(FieldBean bean, String strShowValue, String sql) throws Throwable {
		
		String oldShow = strShowValue;
		
		sql = StringUtil.replace(sql, "${table_schema}", bean.getFk_schema());
		sql = StringUtil.replace(sql, "${table}", bean.getFk_tab());
		sql = StringUtil.replace(sql, "${bpk_field}", bean.getFk_fld_code());
		sql = StringUtil.replace(sql, "${fk_fld_name}", bean.getFk_fld_name());
		sql = StringUtil.replace(sql, "${value}", StringUtil.replace(strShowValue, "'", "''"));
		
		Recordset rs = getDb().get(sql);
		if(rs.getRecordCount() > 0){
			rs.first();
			return rs.getString("tuid");
		}else{
			//先从domain里查找
			if(domainMap != null && domainMap.containsKey(bean.getFk_tab())){
				Map<String, String> domain = domainMap.get(bean.getFk_tab());
				if(domain.containsKey(strShowValue)){
					return domain.get(strShowValue);
				}
			}
			//未匹配到，从错误字段数据映射表中查找
			if(mappingMap.containsKey(bean.getFk_tab())){
				Map<String, String> mapping = mappingMap.get(bean.getFk_tab());
				if(mapping.containsKey(strShowValue)){
					return mapping.get(strShowValue);
				}
			}
		}
		
		throw new Throwable(String.format(ExcelImportUtil.ERROR_FK_ILLEGAL, oldShow));
	}
	
	protected String formatRequestEncoding(String str) throws Throwable {

		Config _config = getConfig();

		// global encoding?
		String encoding = getContext().getInitParameter("request-encoding");
		if (encoding != null && encoding.trim().equalsIgnoreCase(""))
			encoding = null;

		// load resource with appropiate encoding if defined
		if (_config.requestEncoding != null)
			return new String(str.getBytes("ISO8859-1"), _config.requestEncoding);
		else if (encoding != null)
			return new String(str.getBytes("ISO8859-1"), encoding);
		else
			return str;
	}
	
	/**
	 * 验证 正则表达式
	 * 
	 * @author regex 正则表达式 value 所属字符串
	 * @return boolean
	 */
	public boolean regex(String regex, String value) {
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(value);
		return m.find();
	}
	
	public Object keyIsNull(Recordset recordset,String key) throws Throwable{
		return (recordset.containsField(key) && !recordset.isNull(key)) ? recordset.getValue(key) : null;
	}

	/**
	 * Encode reserved javascript characters (\,").<br>
	 * This characters will be replaced by the pre-defined entities.
	 * @param input String that will be processed
	 * @return String with all reserved characters replaced
	 */
	public String encodeJS(String input)
	{
		input = StringUtil.replace(input, "\\", "\\\\");
		input = StringUtil.replace(input, "\"", "\\\"");
		input = StringUtil.replace(input, "'", "\\\'");
		input = StringUtil.replace(input, "\r\n", "\\r\\n");
		input = StringUtil.replace(input, "\n", "\\n");
		
		return input;
	}
	
}
