package com.ccms.imp;

import java.io.Serializable;

import dinamica.Recordset;
import dinamica.StringUtil;

public class FieldBean implements Serializable{

	private static final long serialVersionUID = 6984384384003369421L;
	private Integer tab_id = null;
	private String table_name = null;
	private String field_code = null;
	private String col_name = null;
	private String domain_namespace = null;
	/**
	 * 0-不更新,1-覆盖更新,2-追加更新
	 */
	private String update_mode = null;
	private String field_type = null;
	private Integer field_length = null;
	/**
	 * 0-非空 1-可以为空
	 */
	private String is_mandatory = null;
	/**
	 * 等全部数据处理之后,更新处理
	 */
	private String is_delay = null;
	private String default_value = null;
	private String fk_schema = null;
	private String fk_tab = null;
	private String fk_fld_code = null;
	private String fk_fld_name = null;
	/**
	 * 是否是虚拟字段
	 */
	private String is_virtual_type = null;
	
	/**
	 * 是否为公式（计算）
	 */
	private String is_formula = null;
	/**
	 * 是否保存转化后的代码值（默认保存代码值：1）
	 */
	private String is_save_code = null;
	
	/**
	 * 正则表达式校验
	 */
	private String regex_rule = null;
	/**
	 * 正则表达式校验失败的提示信息
	 */
	private String regex_tip = null;
	
	/**
	 * 预览数据时水平对齐类型
	 */
	private String show_align = null;

	public static FieldBean transformField(Recordset rs) throws Throwable{
		FieldBean bean = new FieldBean();
		bean.setTab_id(rs.getInteger("tab_id"));
		bean.setTable_name(rs.getString("table_name"));
		bean.setField_code(rs.getString("field_code"));
		bean.setCol_name(rs.getString("col_name"));
		bean.setDomain_namespace(rs.getString("domain_namespace"));
		bean.setUpdate_mode(rs.getString("update_mode"));
		bean.setField_type(rs.getString("field_type"));
		String length = rs.getString("field_length");
		if(length != null && length.length() > 0){
			try{
				bean.setField_length(Integer.parseInt(length));
			}catch(Throwable e){
				bean.setField_length(0);
			}
		}else{
			bean.setField_length(0);
		}
		bean.setIs_mandatory(rs.getString("is_mandatory"));
		bean.setIs_delay(rs.getString("is_delay"));
		String value = rs.getString("default_value");
		if(value != null && value.length() > 0){
			//替换 ${DEF 符号
			value = StringUtil.replace(value, "${DEF", "${def");
			bean.setDefault_value(value);
		}
		bean.setFk_schema(rs.getString("fk_schema"));
		bean.setFk_tab(rs.getString("fk_tab"));
		bean.setFk_fld_code(rs.getString("fk_fld_code"));
		bean.setFk_fld_name(rs.getString("fk_fld_name"));
		bean.setIs_virtual_type(rs.getString("is_virtual_type"));
		bean.setIs_formula(rs.getString("is_formula"));
		
		String is_save_code_str =  rs.getString("is_save_code");
		if(is_save_code_str == null || is_save_code_str.trim().length()==0){
			bean.setIs_save_code("1");
		}else{
			bean.setIs_save_code(is_save_code_str);
		}
		
		bean.setRegex_rule(rs.getString("regex_rule"));
		bean.setRegex_tip(rs.getString("regex_tip"));
		//bean.setShow_align(rs.getString("show_align"));
		
		return bean;
	}
	
	public Integer getTab_id() {
		return tab_id;
	}
	public void setTab_id(Integer tabId) {
		tab_id = tabId;
	}
	public String getTable_name() {
		return table_name;
	}
	public void setTable_name(String tableName) {
		table_name = tableName;
	}
	public String getField_code() {
		return field_code;
	}
	public void setField_code(String fieldCode) {
		field_code = fieldCode;
	}
	public String getCol_name() {
		return col_name;
	}
	public void setCol_name(String colName) {
		col_name = colName;
	}
	public String getDomain_namespace() {
		return domain_namespace;
	}
	public void setDomain_namespace(String domainNamespace) {
		domain_namespace = domainNamespace;
	}
	public String getUpdate_mode() {
		return update_mode;
	}
	public void setUpdate_mode(String updateMode) {
		update_mode = updateMode;
	}
	public String getField_type() {
		return field_type;
	}
	public void setField_type(String fieldType) {
		field_type = fieldType;
	}
	public Integer getField_length() {
		return field_length;
	}
	public void setField_length(Integer fieldLength) {
		field_length = fieldLength;
	}
	public String getIs_mandatory() {
		return is_mandatory;
	}
	public void setIs_mandatory(String isMandatory) {
		is_mandatory = isMandatory;
	}
	public String getIs_delay() {
		return is_delay;
	}
	public void setIs_delay(String isDelay) {
		is_delay = isDelay;
	}
	public String getDefault_value() {
		return default_value;
	}
	public void setDefault_value(String defaultValue) {
		default_value = defaultValue;
	}
	public String getFk_schema() {
		return fk_schema;
	}
	public void setFk_schema(String fkSchema) {
		fk_schema = fkSchema;
	}
	public String getFk_tab() {
		return fk_tab;
	}
	public void setFk_tab(String fkTab) {
		fk_tab = fkTab;
	}
	public String getFk_fld_code() {
		return fk_fld_code;
	}
	public void setFk_fld_code(String fkFldCode) {
		fk_fld_code = fkFldCode;
	}
	public String getFk_fld_name() {
		return fk_fld_name;
	}
	public void setFk_fld_name(String fkFldName) {
		fk_fld_name = fkFldName;
	}
	public String getIs_virtual_type() {
		return is_virtual_type;
	}
	public void setIs_virtual_type(String isVirtualType) {
		is_virtual_type = isVirtualType;
	}
	public String getIs_formula() {
		return is_formula;
	}
	public void setIs_formula(String isFormula) {
		is_formula = isFormula;
	}
	public String getIs_save_code() {
		return is_save_code;
	}
	public void setIs_save_code(String isSaveCode) {
		is_save_code = isSaveCode;
	}
	public String getRegex_rule() {
		return regex_rule;
	}
	public void setRegex_rule(String regexRule) {
		regex_rule = regexRule;
	}
	public String getRegex_tip() {
		return regex_tip;
	}
	public void setRegex_tip(String regexTip) {
		regex_tip = regexTip;
	}
	public String getShow_align() {
		return show_align;
	}

	public void setShow_align(String showAlign) {
		show_align = showAlign;
	}
}
