package com.ccms.core.foctory;

import java.io.Serializable;

import dinamica.Recordset;

/**
 * @author zhangchuan
 * 定义表单字段
 *
 */
public class FieldBean implements Serializable{

	private static final long serialVersionUID = -2476173452217033820L;

	/**
	 * 主键
	 */
	private Integer tuid = null;
	/**
	 * 业务表编号
	 */
	private Integer table_id = null;
	/**
	 * 字段中文名称
	 */
	private String field_name_cn = null;
	/**
	 * 字段英文名称
	 */
	private String field_name_en = null;
	/**
	 * 字段code
	 */
	private String field_code = null;
	/**
	 * 字段别名
	 */
	private String field_code_alias = null;
	/**
	 * 字段类型
	 */
	private String field_type = null;
	/**
	 * 字段显示类型
	 */
	private String show_type = null;
	/**
	 * 字段长度
	 */
	private String field_length = null;
	/**
	 * 字段小数点位数
	 */
	private String decimal_length = null;
	/**
	 * 是否非空
	 */
	private String is_mandatory = null;
	/**
	 * 是否虚拟字段
	 */
	private String is_virtual_type = null;
	/**
	 * 字段格式
	 */
	private String format_mark = null;
	/**
	 * 字段子项的命名空间（与外键表互斥且此项优先级高）
	 */
	private String domain_namespace = null;
	/**
	 * 字段外键schema
	 */
	private String fk_schema = null;
	/**
	 * 字段外键表名
	 */
	private String fk_tab = null;
	/**
	 * 字段外键字段
	 */
	private String fk_fld_id = null;
	/**
	 * 字段外键字段别名
	 */
	private String fk_fld_alias = null;
	/**
	 * 外键表意字段(该字段在中英文切换中会作为中文字段使用)
	 */
	private String fk_fld_anchor = null;
	/**
	 * 外键参考字段(该字段在中英文切换中会作为英文字段使用)
	 */
	private String fk_references = null;
	/**
	 * 外键SQL语句.对fk_tab的扩展
	 */
	private String fk_sql = null;
	/**
	 * 插入值时公式(例如${fld:xxxx}-${fld:yyyy})重要提示:所有值插入时都会被转化小写
	 */
	private String insert_phrase = null;
	/**
	 * 修改值时公式 重要提示:所有值更新时都会被转化小写
	 */
	private String update_phrase = null;
	/**
	 * 字段是否为公式
	 */
	private String is_formula = null;
	/**
	 * 事件扩展代码
	 */
	private String plugin_code = null;
	/**
	 * 控件扩展代码(自定义代码)
	 */
	private String plugin_control = null;
	/**
	 * 默认值
	 */
	private String default_value = null;
	/**
	 * 排列顺序
	 */
	private Integer show_order = null;
	/**
	 * 备注
	 */
	private String remark = null;
	
	public FieldBean(){
		
	}
	
	public FieldBean(Recordset rsField) throws Throwable{
		this.tuid = rsField.getInteger("tuid");
		this.table_id = rsField.getInteger("table_id");
		this.field_name_cn = rsField.getString("field_name_cn");
		this.field_name_en = rsField.getString("field_name_en");
		this.field_code = rsField.getString("field_code");
		this.field_code_alias = rsField.getString("field_code_alias");
		this.field_type = rsField.getString("field_type");
		this.show_type = rsField.getString("show_type");
		this.field_length = rsField.getString("field_length");
		this.decimal_length = rsField.getString("decimal_length");
		this.is_mandatory = rsField.getString("is_mandatory");
		this.is_virtual_type = rsField.getString("is_virtual_type");
		this.format_mark = rsField.getString("format_mark");
		this.domain_namespace = rsField.getString("domain_namespace");
		this.fk_schema = rsField.getString("fk_schema");
		this.fk_tab = rsField.getString("fk_tab");
		this.fk_fld_id = rsField.getString("fk_fld_id");
		this.fk_fld_alias = rsField.getString("fk_fld_alias");
		this.fk_fld_anchor = rsField.getString("fk_fld_anchor");
		this.fk_references = rsField.getString("fk_references");
		this.fk_sql = rsField.getString("fk_sql");
		this.insert_phrase = rsField.getString("insert_phrase");
		this.update_phrase = rsField.getString("update_phrase");
		this.is_formula = rsField.getString("is_formula");
		this.plugin_code = rsField.getString("plugin_code");
		this.plugin_control = rsField.getString("plugin_control");
		this.default_value = rsField.getString("default_value");
		this.show_order = rsField.getInteger("show_order");
		this.remark = rsField.getString("remark");
	}
	
	public Integer getTuid() {
		return tuid;
	}
	public void setTuid(Integer tuid) {
		this.tuid = tuid;
	}
	public Integer getTable_id() {
		return table_id;
	}
	public void setTable_id(Integer tableId) {
		table_id = tableId;
	}
	public String getField_name_cn() {
		return field_name_cn;
	}
	public void setField_name_cn(String fieldNameCn) {
		field_name_cn = fieldNameCn;
	}
	public String getField_name_en() {
		return field_name_en;
	}
	public void setField_name_en(String fieldNameEn) {
		field_name_en = fieldNameEn;
	}
	public String getField_code() {
		return field_code;
	}
	public void setField_code(String fieldCode) {
		field_code = fieldCode;
	}
	public String getField_code_alias() {
		return field_code_alias;
	}
	public void setField_code_alias(String fieldCodeAlias) {
		field_code_alias = fieldCodeAlias;
	}
	public String getField_type() {
		return field_type;
	}
	public void setField_type(String fieldType) {
		field_type = fieldType;
	}
	public String getShow_type() {
		return show_type;
	}
	public void setShow_type(String showType) {
		show_type = showType;
	}
	public String getField_length() {
		return field_length;
	}
	public void setField_length(String fieldLength) {
		field_length = fieldLength;
	}
	public String getDecimal_length() {
		return decimal_length;
	}
	public void setDecimal_length(String decimalLength) {
		decimal_length = decimalLength;
	}
	public String getIs_mandatory() {
		return is_mandatory;
	}
	public void setIs_mandatory(String isMandatory) {
		is_mandatory = isMandatory;
	}
	public String getIs_virtual_type() {
		return is_virtual_type;
	}
	public void setIs_virtual_type(String isVirtualType) {
		is_virtual_type = isVirtualType;
	}
	public String getFormat_mark() {
		return format_mark;
	}
	public void setFormat_mark(String formatMark) {
		format_mark = formatMark;
	}
	public String getDomain_namespace() {
		return domain_namespace;
	}
	public void setDomain_namespace(String domainNamespace) {
		domain_namespace = domainNamespace;
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
	public String getFk_fld_id() {
		return fk_fld_id;
	}
	public void setFk_fld_id(String fkFldId) {
		fk_fld_id = fkFldId;
	}
	public String getFk_fld_alias() {
		return fk_fld_alias;
	}
	public void setFk_fld_alias(String fkFldAlias) {
		fk_fld_alias = fkFldAlias;
	}
	public String getFk_fld_anchor() {
		return fk_fld_anchor;
	}
	public void setFk_fld_anchor(String fkFldAnchor) {
		fk_fld_anchor = fkFldAnchor;
	}
	public String getFk_references() {
		return fk_references;
	}
	public void setFk_references(String fkReferences) {
		fk_references = fkReferences;
	}
	public String getFk_sql() {
		return fk_sql;
	}

	public void setFk_sql(String fk_sql) {
		this.fk_sql = fk_sql;
	}

	public String getInsert_phrase() {
		return insert_phrase;
	}
	public void setInsert_phrase(String insertPhrase) {
		insert_phrase = insertPhrase;
	}
	public String getUpdate_phrase() {
		return update_phrase;
	}
	public void setUpdate_phrase(String updatePhrase) {
		update_phrase = updatePhrase;
	}
	public String getIs_formula() {
		return is_formula;
	}
	public void setIs_formula(String isFormula) {
		is_formula = isFormula;
	}
	public String getPlugin_code() {
		return plugin_code;
	}
	public void setPlugin_code(String pluginCode) {
		plugin_code = pluginCode;
	}
	public String getPlugin_control() {
		return plugin_control;
	}
	public void setPlugin_control(String pluginControl) {
		plugin_control = pluginControl;
	}
	public String getDefault_value() {
		return default_value;
	}
	public void setDefault_value(String defaultValue) {
		default_value = defaultValue;
	}
	public Integer getShow_order() {
		return show_order;
	}
	public void setShow_order(Integer showOrder) {
		show_order = showOrder;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
}
