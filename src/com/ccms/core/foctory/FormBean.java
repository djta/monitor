package com.ccms.core.foctory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import dinamica.Recordset;

/**
 * @author zhangchuan
 * 定义表单
 *
 */
public class FormBean implements Serializable{

	private static final long serialVersionUID = 3852031291935408477L;
	
	private Integer form_id = null;
	private Integer subject_id = null;
	private Integer table_id = null;
	private Integer pid = null;
	private String form_name_cn = null;
	private String form_name_en = null;
	private Integer col_num_edit = null;
	private Integer col_num_filter = null;
	private String form_type = null;
	private String checkfield = null;
	private String keypress = null;
	private String form_js = null;
	private String addnew_js = null;
	private String loadfilter_js = null;
	private String loadeditor_js = null;
	private String editback_js = null;
	private String insert_js = null;
	private String update_js = null;
	private String search_success_js = null;
	
	private String insert_classname = null;
	private String insert_classname1 = null;
	private String insert_classname_replace = null;
	private String insert_classname_validator = null;
	private String update_classname = null;
	private String update_classname1 = null;
	private String update_classname_replace = null;
	private String update_classname_validator = null;
	private String delete_classname_validator = null;
	private String delete_classname_replace = null;
	private String delete_classname = null;
	
	//view
	private String viewTitleCn = null;
	private String viewTitleEn = null;
	private String viewField = null;
	private String viewJson = null;
	
	//filter
	private String filterControlsCn = null;
	private String filterControlsEn = null;
	
	//form
	private String formControlsCn = null;
	private String formControlsEn = null;
	
	private Recordset rsForm = null;
	
	private Recordset queryFilterField = null;
	private Recordset queryShowField = null;
	private Recordset queryExcelField = null;
	private Recordset queryPdfField = null;
	
	private Recordset insertQueryField = null;
	private Recordset updateQueryField = null;

	private Recordset viewQueryGridField = null;
	private Recordset searchQueryTotal = null;
	
	
	/**
	 * 查询级联字段
	 */
	private Recordset queryCascadeField = null;
	
	//table
	private String table_code = null;
	private String table_alias = null;
	private String bpk_field = null;
	private String delete_field = null;
	private String bpk_field_prefix = null;
	private String bpk_field_seq = null;
	private String bpk_field_type = null;

	//delete
	/**
	 * 删除语句
	 */
	private String delete_sql = null;
	
	//edit
	/**
	 * 自定义编辑sql
	 */
	private String edit_sql = null;
	/**
	 * 最终执行的js语句
	 */
	private String edit_exce_js = null;
	private String edit_exce_item_js = null;
	
	//insert
	/**
	 * 新增原始sql
	 */
	private String insert_sql = null;
	
	/**
	 * 新增时序列取值
	 */
	private String insert_query_bpk_field_sql = null;
	
	//update
	/**
	 * 修改原始sql
	 */
	private String update_sql = null;
	
	/**
	 * 查询快照语句
	 */
	private String query_snapshot_sql = null;
	//search
	/**
	 * 查询sql
	 */
	private String search_sql = null;
	/**
	 * 查询sql中文
	 */
	private String search_sql_cn = null;
	/**
	 * 查询sql英文
	 */
	private String search_sql_en = null;
	/**
	 * 处理后查询总数的语句
	 */
	private String search_count_sql = null;

	/**
	 * 
	 */
	private String search_orderby_cn = null;
	private String search_orderby_en = null;
	
	/**
	 * 增加审计
	 */
	private String insert_audit_sql = null;
	private String update_audit_sql = null;
	private String delete_audit_sql = null;
	private String excel_audit_sql = null;
	
	/**
	 * 版本还原时语句
	 */
	private String edit_query_data_log = null;
	
	/**
	 * 表单权限
	 */
	private String oper_priviledge = null;
	private Map<String, String> skillMap = new HashMap<String, String>();

	private Map<Integer, FormItemBean> itemMap = new HashMap<Integer, FormItemBean>();
	
	private String owner_field = null;
	private String access_type = null;
	private String operation_type = null;
	private Integer page_size = null;
	private String form_action = null;
	private String search_action = null;
	

	public Recordset getFormRecordset(String locale) throws Throwable{
		Recordset rs = new Recordset();
		
		return rs;
	}
	
	public FormBean(Recordset rsForm) throws Throwable{
		this.form_id = rsForm.getInteger("form_id");
		this.subject_id = rsForm.getInteger("subject_id");
		this.table_id = rsForm.getInteger("table_id");
		this.form_name_cn = rsForm.getString("form_name_cn");
		this.form_name_en = rsForm.getString("form_name_en");
		this.col_num_edit = rsForm.getInteger("col_num_edit");
		this.col_num_filter = rsForm.getInteger("col_num_filter");
		this.checkfield = rsForm.getString("checkfield");
		this.keypress = rsForm.getString("keypress");
		this.form_js = rsForm.getString("form_js");
		this.addnew_js = rsForm.getString("addnew_js");
		this.loadfilter_js = rsForm.getString("loadfilter_js");
		this.loadeditor_js = rsForm.getString("loadeditor_js");
		this.editback_js = rsForm.getString("editback_js");
		this.insert_js = rsForm.getString("insert_js");
		this.update_js = rsForm.getString("update_js");
		this.search_success_js = rsForm.getString("search_success_js");
		this.search_sql = rsForm.getString("search_sql");
		this.edit_sql = rsForm.getString("edit_sql");
		this.insert_classname = rsForm.getString("insert_classname");
		this.update_classname = rsForm.getString("update_classname");
		this.delete_classname = rsForm.getString("delete_classname");
		this.insert_classname1 = rsForm.getString("insert_classname1");
		this.update_classname1 = rsForm.getString("update_classname1");
		this.insert_classname_replace = rsForm.getString("insert_classname_replace");
		this.update_classname_replace = rsForm.getString("update_classname_replace");
		this.delete_classname_replace = rsForm.getString("delete_classname_replace");
		this.insert_classname_validator = rsForm.getString("insert_classname_validator");
		this.update_classname_validator = rsForm.getString("update_classname_validator");
		this.delete_classname_validator = rsForm.getString("delete_classname_validator");
		
		this.table_id = rsForm.getInteger("table_id");
		this.table_code = rsForm.getString("table_code");
		this.table_alias = rsForm.getString("table_alias");
		this.bpk_field = rsForm.getString("bpk_field");
		this.bpk_field_prefix = rsForm.getString("bpk_field_prefix");
		this.bpk_field_seq = rsForm.getString("bpk_field_seq");
		this.delete_field = rsForm.getString("delete_field");

		this.oper_priviledge = rsForm.getString("oper_priviledge");
		this.owner_field = rsForm.getString("owner_field");
		this.access_type = rsForm.getString("access_type");
		this.operation_type = rsForm.getString("operation_type");
		this.page_size = rsForm.getInteger("page_size");
		this.form_action = rsForm.getString("form_action");
		this.search_action = rsForm.getString("search_action");
		
		this.rsForm = rsForm;
	}
	
	
	public Recordset getViewQueryGridField() {
		return viewQueryGridField;
	}

	public void setViewQueryGridField(Recordset viewQueryGridField) {
		this.viewQueryGridField = viewQueryGridField;
	}

	public Recordset getSearchQueryTotal() {
		return searchQueryTotal;
	}

	public void setSearchQueryTotal(Recordset searchQueryTotal) {
		this.searchQueryTotal = searchQueryTotal;
	}

	public Map<Integer, FormItemBean> getItemMap() {
		return itemMap;
	}

	public void setItemMap(Map<Integer, FormItemBean> itemMap) {
		this.itemMap = itemMap;
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
	public Integer getPid() {
		return pid;
	}
	public void setPid(Integer pid) {
		this.pid = pid;
	}
	public String getForm_name_cn() {
		return form_name_cn;
	}
	public void setForm_name_cn(String formNameCn) {
		form_name_cn = formNameCn;
	}
	public String getForm_name_en() {
		return form_name_en;
	}
	public void setForm_name_en(String formNameEn) {
		form_name_en = formNameEn;
	}
	public Integer getCol_num_edit() {
		return col_num_edit;
	}
	public void setCol_num_edit(Integer colNumEdit) {
		col_num_edit = colNumEdit;
	}
	public Integer getCol_num_filter() {
		return col_num_filter;
	}
	public void setCol_num_filter(Integer colNumFilter) {
		col_num_filter = colNumFilter;
	}
	public String getForm_type() {
		return form_type;
	}
	public void setForm_type(String formType) {
		form_type = formType;
	}
	public String getCheckfield() {
		return checkfield;
	}
	public void setCheckfield(String checkfield) {
		this.checkfield = checkfield;
	}
	public String getKeypress() {
		return keypress;
	}
	public void setKeypress(String keypress) {
		this.keypress = keypress;
	}
	public String getForm_js() {
		return form_js;
	}
	public void setForm_js(String formJs) {
		form_js = formJs;
	}
	public String getAddnew_js() {
		return addnew_js;
	}
	public void setAddnew_js(String addnewJs) {
		addnew_js = addnewJs;
	}
	public String getLoadfilter_js() {
		return loadfilter_js;
	}
	public void setLoadfilter_js(String loadfilterJs) {
		loadfilter_js = loadfilterJs;
	}
	public String getLoadeditor_js() {
		return loadeditor_js;
	}
	public void setLoadeditor_js(String loadeditorJs) {
		loadeditor_js = loadeditorJs;
	}
	public String getEditback_js() {
		return editback_js;
	}
	public void setEditback_js(String editbackJs) {
		editback_js = editbackJs;
	}
	public String getInsert_js() {
		return insert_js;
	}
	public void setInsert_js(String insertJs) {
		insert_js = insertJs;
	}
	public String getUpdate_js() {
		return update_js;
	}
	public void setUpdate_js(String updateJs) {
		update_js = updateJs;
	}
	public String getSearch_sql() {
		return search_sql;
	}
	public void setSearch_sql(String searchSql) {
		search_sql = searchSql;
	}
	public String getEdit_sql() {
		return edit_sql;
	}
	public void setEdit_sql(String editSql) {
		edit_sql = editSql;
	}

	public String getInsert_classname() {
		return insert_classname;
	}
	public void setInsert_classname(String insertClassname) {
		insert_classname = insertClassname;
	}
	public String getUpdate_classname() {
		return update_classname;
	}
	public void setUpdate_classname(String updateClassname) {
		update_classname = updateClassname;
	}
	public String getDelete_classname() {
		return delete_classname;
	}
	public void setDelete_classname(String deleteClassname) {
		delete_classname = deleteClassname;
	}
	public String getInsert_classname1() {
		return insert_classname1;
	}
	public void setInsert_classname1(String insertClassname1) {
		insert_classname1 = insertClassname1;
	}
	public String getUpdate_classname1() {
		return update_classname1;
	}
	public void setUpdate_classname1(String updateClassname1) {
		update_classname1 = updateClassname1;
	}
	public String getInsert_classname_replace() {
		return insert_classname_replace;
	}
	public void setInsert_classname_replace(String insertClassnameReplace) {
		insert_classname_replace = insertClassnameReplace;
	}
	public String getUpdate_classname_replace() {
		return update_classname_replace;
	}
	public void setUpdate_classname_replace(String updateClassnameReplace) {
		update_classname_replace = updateClassnameReplace;
	}
	public String getDelete_classname_replace() {
		return delete_classname_replace;
	}
	public void setDelete_classname_replace(String deleteClassnameReplace) {
		delete_classname_replace = deleteClassnameReplace;
	}
	public String getInsert_classname_validator() {
		return insert_classname_validator;
	}
	public void setInsert_classname_validator(String insertClassnameValidator) {
		insert_classname_validator = insertClassnameValidator;
	}
	public String getUpdate_classname_validator() {
		return update_classname_validator;
	}
	public void setUpdate_classname_validator(String updateClassnameValidator) {
		update_classname_validator = updateClassnameValidator;
	}
	public String getDelete_classname_validator() {
		return delete_classname_validator;
	}
	public void setDelete_classname_validator(String deleteClassnameValidator) {
		delete_classname_validator = deleteClassnameValidator;
	}

	public Integer getForm_id() {
		return form_id;
	}

	public void setForm_id(Integer formId) {
		form_id = formId;
	}

	public String getViewTitleCn() {
		return viewTitleCn;
	}

	public void setViewTitleCn(String viewTitleCn) {
		this.viewTitleCn = viewTitleCn;
	}

	public String getViewTitleEn() {
		return viewTitleEn;
	}

	public void setViewTitleEn(String viewTitleEn) {
		this.viewTitleEn = viewTitleEn;
	}

	public String getViewField() {
		return viewField;
	}

	public void setViewField(String viewField) {
		this.viewField = viewField;
	}

	public String getFilterControlsCn() {
		return filterControlsCn;
	}

	public void setFilterControlsCn(String filterControlsCn) {
		this.filterControlsCn = filterControlsCn;
	}

	public String getFilterControlsEn() {
		return filterControlsEn;
	}

	public void setFilterControlsEn(String filterControlsEn) {
		this.filterControlsEn = filterControlsEn;
	}

	public String getFormControlsCn() {
		return formControlsCn;
	}

	public void setFormControlsCn(String formControlsCn) {
		this.formControlsCn = formControlsCn;
	}

	public String getFormControlsEn() {
		return formControlsEn;
	}

	public void setFormControlsEn(String formControlsEn) {
		this.formControlsEn = formControlsEn;
	}

	public Recordset getRsForm() {
		return rsForm;
	}

	public Recordset getQueryExcelField() {
		return queryExcelField;
	}

	public void setQueryExcelField(Recordset queryExcelField) {
		this.queryExcelField = queryExcelField;
	}

	public Recordset getQueryPdfField() {
		return queryPdfField;
	}

	public void setQueryPdfField(Recordset queryPdfField) {
		this.queryPdfField = queryPdfField;
	}

	public Recordset getInsertQueryField() {
		return insertQueryField;
	}

	public void setInsertQueryField(Recordset insertQueryField) {
		this.insertQueryField = insertQueryField;
	}

	public Recordset getUpdateQueryField() {
		return updateQueryField;
	}

	public void setUpdateQueryField(Recordset updateQueryField) {
		this.updateQueryField = updateQueryField;
	}

	public Recordset getQueryCascadeField() {
		return queryCascadeField;
	}

	public void setQueryCascadeField(Recordset queryCascadeField) {
		this.queryCascadeField = queryCascadeField;
	}

	public String getTable_code() {
		return table_code;
	}

	public void setTable_code(String tableCode) {
		table_code = tableCode;
	}

	public String getTable_alias() {
		return table_alias;
	}

	public void setTable_alias(String tableAlias) {
		table_alias = tableAlias;
	}

	public String getBpk_field() {
		return bpk_field;
	}

	public void setBpk_field(String bpkField) {
		bpk_field = bpkField;
	}

	public String getBpk_field_type() {
		return bpk_field_type;
	}

	public void setBpk_field_type(String bpkFieldType) {
		bpk_field_type = bpkFieldType;
	}

	public String getDelete_field() {
		return delete_field;
	}

	public void setDelete_field(String deleteField) {
		delete_field = deleteField;
	}

	public String getDelete_sql() {
		return delete_sql;
	}

	public void setDelete_sql(String deleteSql) {
		delete_sql = deleteSql;
	}

	public String getInsert_sql() {
		return insert_sql;
	}

	public void setInsert_sql(String insertSql) {
		insert_sql = insertSql;
	}

	public String getUpdate_sql() {
		return update_sql;
	}

	public void setUpdate_sql(String updateSql) {
		update_sql = updateSql;
	}

	public String getBpk_field_seq() {
		return bpk_field_seq;
	}

	public void setBpk_field_seq(String bpkFieldSeq) {
		bpk_field_seq = bpkFieldSeq;
	}

	public String getEdit_exce_item_js() {
		return edit_exce_item_js;
	}

	public void setEdit_exce_item_js(String edit_exce_item_js) {
		this.edit_exce_item_js = edit_exce_item_js;
	}

	public String getEdit_exce_js() {
		return edit_exce_js;
	}

	public void setEdit_exce_js(String editExceJs) {
		edit_exce_js = editExceJs;
	}

	public String getSearch_sql_cn() {
		return search_sql_cn;
	}

	public void setSearch_sql_cn(String searchSqlCn) {
		search_sql_cn = searchSqlCn;
	}

	public String getSearch_sql_en() {
		return search_sql_en;
	}

	public void setSearch_sql_en(String searchSqlEn) {
		search_sql_en = searchSqlEn;
	}

	public Recordset getQueryFilterField() {
		return queryFilterField;
	}

	public void setQueryFilterField(Recordset queryFilterField) {
		this.queryFilterField = queryFilterField;
	}

	public Recordset getQueryShowField() {
		return queryShowField;
	}

	public void setQueryShowField(Recordset queryShowField) {
		this.queryShowField = queryShowField;
	}
	
	public String getSearch_count_sql() {
		return search_count_sql;
	}

	public void setSearch_count_sql(String searchCountSql) {
		search_count_sql = searchCountSql;
	}

	public String getSearch_orderby_cn() {
		return search_orderby_cn;
	}

	public void setSearch_orderby_cn(String search_orderby_cn) {
		this.search_orderby_cn = search_orderby_cn;
	}

	public String getSearch_orderby_en() {
		return search_orderby_en;
	}

	public void setSearch_orderby_en(String search_orderby_en) {
		this.search_orderby_en = search_orderby_en;
	}

	public String getInsert_audit_sql() {
		return insert_audit_sql;
	}

	public void setInsert_audit_sql(String insertAuditSql) {
		insert_audit_sql = insertAuditSql;
	}

	public String getUpdate_audit_sql() {
		return update_audit_sql;
	}

	public void setUpdate_audit_sql(String updateAuditSql) {
		update_audit_sql = updateAuditSql;
	}

	public String getDelete_audit_sql() {
		return delete_audit_sql;
	}

	public void setDelete_audit_sql(String deleteAuditSql) {
		delete_audit_sql = deleteAuditSql;
	}

	public String getExcel_audit_sql() {
		return excel_audit_sql;
	}

	public void setExcel_audit_sql(String excelAuditSql) {
		excel_audit_sql = excelAuditSql;
	}

	public String getQuery_snapshot_sql() {
		return query_snapshot_sql;
	}

	public void setQuery_snapshot_sql(String querySnapshotSql) {
		query_snapshot_sql = querySnapshotSql;
	}

	public String getOper_priviledge() {
		return oper_priviledge;
	}

	public void setOper_priviledge(String operPriviledge) {
		oper_priviledge = operPriviledge;
	}

	public Map<String, String> getSkillMap() {
		return skillMap;
	}

	public void setSkillMap(Map<String, String> skillMap) {
		this.skillMap = skillMap;
	}

	public String getEdit_query_data_log() {
		return edit_query_data_log;
	}

	public void setEdit_query_data_log(String editQueryDataLog) {
		edit_query_data_log = editQueryDataLog;
	}

	public String getBpk_field_prefix() {
		return bpk_field_prefix;
	}

	public void setBpk_field_prefix(String bpkFieldPrefix) {
		bpk_field_prefix = bpkFieldPrefix;
	}

	public String getInsert_query_bpk_field_sql() {
		return insert_query_bpk_field_sql;
	}

	public void setInsert_query_bpk_field_sql(String insertQueryBpkFieldSql) {
		insert_query_bpk_field_sql = insertQueryBpkFieldSql;
	}

	public String getOwner_field() {
		return owner_field;
	}

	public void setOwner_field(String ownerField) {
		owner_field = ownerField;
	}

	public String getAccess_type() {
		return access_type;
	}

	public void setAccess_type(String accessType) {
		access_type = accessType;
	}

	public String getOperation_type() {
		return operation_type;
	}

	public void setOperation_type(String operationType) {
		operation_type = operationType;
	}

	public Integer getPage_size() {
		return page_size;
	}

	public void setPage_size(Integer pageSize) {
		page_size = pageSize;
	}

	public String getForm_action() {
		return form_action;
	}

	public void setForm_action(String formAction) {
		form_action = formAction;
	}

	public String getSearch_action() {
		return search_action;
	}

	public void setSearch_action(String searchAction) {
		search_action = searchAction;
	}

	public String getViewJson() {
		return viewJson;
	}

	public void setViewJson(String viewJson) {
		this.viewJson = viewJson;
	}

	public String getSearch_success_js() {
		return search_success_js;
	}

	public void setSearch_success_js(String search_success_js) {
		this.search_success_js = search_success_js;
	}
}
