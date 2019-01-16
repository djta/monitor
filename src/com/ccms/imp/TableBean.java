package com.ccms.imp;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import dinamica.Recordset;

public class TableBean implements Serializable{

	private static final long serialVersionUID = 6255069022624072543L;
	private Integer tab_id = null;
	private Integer parent_id = null;
	private String bpk_field_alias = null;
	private String if_new_flag = null;
	private String schema_name = null;
	private Integer subject_id = null;
	private Integer table_id = null;
	private String table_code = null;
	private String bpk_field = null;
	private String bpk_field_prefix = null;
	private String bpk_field_seq = null;
	private String bpk_field_type = null;
	private String data_operator_type = null;
	private Set<TableBean> childTable = new HashSet<TableBean>();
	private Set<FieldBean> fieldSet = new HashSet<FieldBean>();
	private Set<RuleBean> ruleSet = new HashSet<RuleBean>();
	
	public void addFieldBean(FieldBean bean){
		this.fieldSet.add(bean);
	}
	
	public void addChildTable(TableBean bean){
		this.childTable.add(bean);
	}
	
	public void addRule(RuleBean rule){
		this.ruleSet.add(rule);
	}
	
	public Set<TableBean> getChildTable() {
		return childTable;
	}
	public Set<FieldBean> getFieldSet() {
		return fieldSet;
	}
	public Set<RuleBean> getRuleSet() {
		return ruleSet;
	}

	public static TableBean transformTable(Recordset rs) throws Throwable{
		TableBean bean = new TableBean();
		bean.setTab_id((Integer) keyIsNull(rs, "tab_id"));
		bean.setParent_id((Integer) keyIsNull(rs, "parent_id")==null?null:(Integer) keyIsNull(rs, "parent_id"));
		bean.setBpk_field_alias((String) keyIsNull(rs, "bpk_field_alias"));
		bean.setIf_new_flag((String) keyIsNull(rs, "if_new_flag"));
		bean.setSchema_name((String) keyIsNull(rs, "schema_name"));
		bean.setTable_id((Integer) keyIsNull(rs, "table_id"));
		bean.setSubject_id((Integer) keyIsNull(rs, "subject_id"));
		bean.setTable_code((String) keyIsNull(rs, "table_code"));
		bean.setBpk_field((String) keyIsNull(rs, "bpk_field"));
		bean.setBpk_field_prefix((String) keyIsNull(rs, "bpk_field_prefix"));
		bean.setBpk_field_seq((String) keyIsNull(rs, "bpk_field_seq"));
		bean.setBpk_field_type((String) keyIsNull(rs, "bpk_field_type"));
		bean.setData_operator_type((String) keyIsNull(rs, "data_operator_type"));
		return bean;
	}
	
	public static Object keyIsNull(Recordset recordset,String key) throws Throwable{
		return (recordset.containsField(key) && !recordset.isNull(key)) ? recordset.getValue(key) : null;
	}
	
	public Integer getSubject_id() {
		return subject_id;
	}

	public void setSubject_id(Integer subjectId) {
		subject_id = subjectId;
	}

	public Integer getTable_id() {
		return table_id;
	}

	public void setTable_id(Integer tableId) {
		table_id = tableId;
	}
	public Integer getTab_id() {
		return tab_id;
	}
	public void setTab_id(Integer tabId) {
		tab_id = tabId;
	}
	public Integer getParent_id() {
		return parent_id;
	}
	public void setParent_id(Integer parentId) {
		parent_id = parentId;
	}
	public String getBpk_field_alias() {
		return bpk_field_alias;
	}
	public void setBpk_field_alias(String bpkFieldAlias) {
		bpk_field_alias = bpkFieldAlias;
	}
	public String getIf_new_flag() {
		return if_new_flag;
	}
	public void setIf_new_flag(String ifNewFlag) {
		if_new_flag = ifNewFlag;
	}
	public String getSchema_name() {
		return schema_name;
	}
	public void setSchema_name(String schemaName) {
		schema_name = schemaName;
	}
	public String getTable_code() {
		return table_code;
	}
	public void setTable_code(String tableCode) {
		table_code = tableCode;
	}
	public String getBpk_field() {
		return bpk_field;
	}
	public void setBpk_field(String bpkField) {
		bpk_field = bpkField;
	}
	public String getBpk_field_prefix() {
		return bpk_field_prefix;
	}
	public void setBpk_field_prefix(String bpkFieldPrefix) {
		bpk_field_prefix = bpkFieldPrefix;
	}
	public String getBpk_field_seq() {
		return bpk_field_seq;
	}
	public void setBpk_field_seq(String bpkFieldSeq) {
		bpk_field_seq = bpkFieldSeq;
	}
	public String getBpk_field_type() {
		return bpk_field_type;
	}
	public void setBpk_field_type(String bpkFieldType) {
		bpk_field_type = bpkFieldType;
	}
	public String getData_operator_type() {
		return data_operator_type;
	}
	public void setData_operator_type(String dataOperatorType) {
		data_operator_type = dataOperatorType;
	}
}
