package com.ccms.core.foctory;

import java.io.Serializable;

import javax.servlet.ServletContext;

import dinamica.StringUtil;

/**
 * @author zhangchuan
 * 存储模板文件
 *
 */
public class TemplateBean implements Serializable {

	private static final long serialVersionUID = -254379139254615059L;
	
	//加载模版
    
    //view
	public String tTh  = null;
    public String tTd  = null;
    public String tJson = null;
    
    //form
    public String tRow  = null;
    public String tItem  = null;
    public String tItemNone  = null;
    public String tCheckbox = null;
    public String tRadiobox = null;
    public String tText = null;
    public String tCombo = null;
    public String tDate = null;
    public String tDateTime = null;
    public String tHidden = null;
    public String tTextReadonly = null;
    public String tTextShowonly = null;
    public String tTextarea = null;
    public String tPickup = null;
    public String tPlugin = null;
    public String tEmpty = null;
    
    public String queryFilterField = null;
    public String queryShowField = null;
    public String formQueryFieldSql = null;
    public String queryCascadeSql = null;
    public String filterQueryFieldSql = null;
    public String queryForm = null;
    public String queryDocument = null;
    public String queryDocumentParams = null;
    public String queryItem = null;
    public String viewQueryGridField = null;
    
    public String deleteSql = null;
    public String deleteUpdateSql = null;
    public String insertSql = null;
    public String updateSql = null;
    public String insertQueryFieldSql = null;
    public String insertQueryBpkFieldSql = null;
    public String updateQuerySnapshotSql = null;
    public String updateQueryFieldSql = null;
    public String searchSql = null;
    public String searchQueryGridFieldSql = null;
    
    public String editSql = null;
    public String editQueryField = null;
    public String editQueryComboField = null;
    public String editQueryShowField = null;
    public String eidtQueryCheckboxField = null;
    public String editRow = null;
    public String editRowHidden = null;
    public String editRowCheckbox = null;
    public String editRowCombo = null;
    public String editRowMulitCheckbox = null;
    public String showRow = null;
    public String editQueryDataLog = null;

    public String queryExcelFieldSql = null;
    public String queryPdfFieldSql = null;
    
    public String insertAuditSql = null;
    
    //filter
    public String tRowFilter  = null;
    public String tItemFilter  = null;
    public String tItemNoneFilter  = null;
    public String tCheckboxFilter = null;
    public String tRadioboxFilter = null;
    public String tTextFilter = null;
    public String tComboFilter = null;
    public String tDateFilter = null;
    public String tDateTimeFilter = null;
    public String tHiddenFilter = null;
    public String tTextReadonlyFilter = null;
    public String tTextShowonlyFilter = null;
    public String tTextareaFilter = null;
    public String tPickupFilter = null;
    public String tPluginFilter = null;
    public String tEmptyFilter = null;
    
    public String strDomainCn = null;
    public String strDomainEn = null;
    public String strFkField = null;
    public String strFkDataCn = null;
    public String strFkDataEn = null;
    
    public ServletContext _ctx = null;
    
	public String getEditQueryShowField() {
		return editQueryShowField;
	}

	public void setEditQueryShowField(String editQueryShowField) {
		this.editQueryShowField = editQueryShowField;
	}

	public String getQueryDocument() {
		return queryDocument;
	}

	public void setQueryDocument(String queryDocument) {
		this.queryDocument = queryDocument;
	}

	public String gettRow() {
		return tRow;
	}

	public void settRow(String tRow) {
		this.tRow = tRow;
	}

	public String gettItem() {
		return tItem;
	}

	public void settItem(String tItem) {
		this.tItem = tItem;
	}

	public String gettCheckbox() {
		return tCheckbox;
	}

	public void settCheckbox(String tCheckbox) {
		this.tCheckbox = tCheckbox;
	}

	public String gettRadiobox() {
		return tRadiobox;
	}

	public void settRadiobox(String tRadiobox) {
		this.tRadiobox = tRadiobox;
	}

	public String gettText() {
		return tText;
	}

	public void settText(String tText) {
		this.tText = tText;
	}

	public String gettCombo() {
		return tCombo;
	}

	public void settCombo(String tCombo) {
		this.tCombo = tCombo;
	}

	public String gettDate() {
		return tDate;
	}

	public void settDate(String tDate) {
		this.tDate = tDate;
	}

	public String gettDateTime() {
		return tDateTime;
	}

	public void settDateTime(String tDateTime) {
		this.tDateTime = tDateTime;
	}

	public String gettDateTimeFilter() {
		return tDateTimeFilter;
	}

	public void settDateTimeFilter(String tDateTimeFilter) {
		this.tDateTimeFilter = tDateTimeFilter;
	}

	public String gettHidden() {
		return tHidden;
	}

	public void settHidden(String tHidden) {
		this.tHidden = tHidden;
	}

	public String gettTextReadonly() {
		return tTextReadonly;
	}

	public void settTextReadonly(String tTextReadonly) {
		this.tTextReadonly = tTextReadonly;
	}

	public String gettTextarea() {
		return tTextarea;
	}

	public void settTextarea(String tTextarea) {
		this.tTextarea = tTextarea;
	}

	public String gettPickup() {
		return tPickup;
	}

	public void settPickup(String tPickup) {
		this.tPickup = tPickup;
	}

	public String gettPlugin() {
		return tPlugin;
	}

	public void settPlugin(String tPlugin) {
		this.tPlugin = tPlugin;
	}

	public String gettEmpty() {
		return tEmpty;
	}

	public void settEmpty(String tEmpty) {
		this.tEmpty = tEmpty;
	}

	public String getStrDomainCn() {
		return strDomainCn;
	}

	public void setStrDomainCn(String strDomainCn) {
		this.strDomainCn = strDomainCn;
	}

	public String getStrDomainEn() {
		return strDomainEn;
	}

	public void setStrDomainEn(String strDomainEn) {
		this.strDomainEn = strDomainEn;
	}

	public String getStrFkField() {
		return strFkField;
	}

	public void setStrFkField(String strFkField) {
		this.strFkField = strFkField;
	}

	public String getStrFkDataCn() {
		return strFkDataCn;
	}

	public void setStrFkDataCn(String strFkDataCn) {
		this.strFkDataCn = strFkDataCn;
	}

	public String getStrFkDataEn() {
		return strFkDataEn;
	}

	public void setStrFkDataEn(String strFkDataEn) {
		this.strFkDataEn = strFkDataEn;
	}

    public String gettTh() {
		return tTh;
	}

	public void settTh(String tTh) {
		this.tTh = tTh;
	}

	public String gettTd() {
		return tTd;
	}

	public void settTd(String tTd) {
		this.tTd = tTd;
	}

	public String gettCheckboxFilter() {
		return tCheckboxFilter;
	}

	public void settCheckboxFilter(String tCheckboxFilter) {
		this.tCheckboxFilter = tCheckboxFilter;
	}

	public String gettRadioboxFilter() {
		return tRadioboxFilter;
	}

	public void settRadioboxFilter(String tRadioboxFilter) {
		this.tRadioboxFilter = tRadioboxFilter;
	}

	public String gettTextFilter() {
		return tTextFilter;
	}

	public void settTextFilter(String tTextFilter) {
		this.tTextFilter = tTextFilter;
	}

	public String gettComboFilter() {
		return tComboFilter;
	}

	public void settComboFilter(String tComboFilter) {
		this.tComboFilter = tComboFilter;
	}

	public String gettDateFilter() {
		return tDateFilter;
	}

	public void settDateFilter(String tDateFilter) {
		this.tDateFilter = tDateFilter;
	}

	public String gettHiddenFilter() {
		return tHiddenFilter;
	}

	public void settHiddenFilter(String tHiddenFilter) {
		this.tHiddenFilter = tHiddenFilter;
	}

	public String gettTextReadonlyFilter() {
		return tTextReadonlyFilter;
	}

	public void settTextReadonlyFilter(String tTextReadonlyFilter) {
		this.tTextReadonlyFilter = tTextReadonlyFilter;
	}

	public String gettTextareaFilter() {
		return tTextareaFilter;
	}

	public void settTextareaFilter(String tTextareaFilter) {
		this.tTextareaFilter = tTextareaFilter;
	}

	public String gettPickupFilter() {
		return tPickupFilter;
	}

	public void settPickupFilter(String tPickupFilter) {
		this.tPickupFilter = tPickupFilter;
	}

	public String gettPluginFilter() {
		return tPluginFilter;
	}

	public void settPluginFilter(String tPluginFilter) {
		this.tPluginFilter = tPluginFilter;
	}

	public String gettEmptyFilter() {
		return tEmptyFilter;
	}

	public void settEmptyFilter(String tEmptyFilter) {
		this.tEmptyFilter = tEmptyFilter;
	}

	public String gettRowFilter() {
		return tRowFilter;
	}

	public void settRowFilter(String tRowFilter) {
		this.tRowFilter = tRowFilter;
	}

	public String gettItemFilter() {
		return tItemFilter;
	}

	public void settItemFilter(String tItemFilter) {
		this.tItemFilter = tItemFilter;
	}

	public String getFormQueryFieldSql() {
		return formQueryFieldSql;
	}

	public void setFormQueryFieldSql(String formQueryFieldSql) {
		this.formQueryFieldSql = formQueryFieldSql;
	}

	public String getQueryItem() {
		return queryItem;
	}

	public void setQueryItem(String queryItem) {
		this.queryItem = queryItem;
	}

	public String getFilterQueryFieldSql() {
		return filterQueryFieldSql;
	}

	public void setFilterQueryFieldSql(String filterQueryFieldSql) {
		this.filterQueryFieldSql = filterQueryFieldSql;
	}

	public String getQueryForm() {
		return queryForm;
	}

	public void setQueryForm(String queryForm) {
		this.queryForm = queryForm;
	}

	public String getViewQueryGridField() {
		return viewQueryGridField;
	}

	public void setViewQueryGridField(String viewQueryGridField) {
		this.viewQueryGridField = viewQueryGridField;
	}

	public String getDeleteSql() {
		return deleteSql;
	}

	public void setDeleteSql(String deleteSql) {
		this.deleteSql = deleteSql;
	}

	public String getDeleteUpdateSql() {
		return deleteUpdateSql;
	}

	public void setDeleteUpdateSql(String deleteUpdateSql) {
		this.deleteUpdateSql = deleteUpdateSql;
	}

	public String getInsertSql() {
		return insertSql;
	}

	public void setInsertSql(String insertSql) {
		this.insertSql = insertSql;
	}

	public String getUpdateSql() {
		return updateSql;
	}

	public void setUpdateSql(String updateSql) {
		this.updateSql = updateSql;
	}

	public String getInsertQueryFieldSql() {
		return insertQueryFieldSql;
	}

	public void setInsertQueryFieldSql(String insertQueryFieldSql) {
		this.insertQueryFieldSql = insertQueryFieldSql;
	}

	public String getUpdateQueryFieldSql() {
		return updateQueryFieldSql;
	}

	public void setUpdateQueryFieldSql(String updateQueryFieldSql) {
		this.updateQueryFieldSql = updateQueryFieldSql;
	}

	public String getQueryCascadeSql() {
		return queryCascadeSql;
	}

	public void setQueryCascadeSql(String queryCascadeSql) {
		this.queryCascadeSql = queryCascadeSql;
	}

	public String getEditSql() {
		return editSql;
	}

	public void setEditSql(String editSql) {
		this.editSql = editSql;
	}

	public String getEditQueryField() {
		return editQueryField;
	}

	public void setEditQueryField(String editQueryField) {
		this.editQueryField = editQueryField;
	}

	public String getEditQueryComboField() {
		return editQueryComboField;
	}

	public void setEditQueryComboField(String editQueryComboField) {
		this.editQueryComboField = editQueryComboField;
	}

	public String getEidtQueryCheckboxField() {
		return eidtQueryCheckboxField;
	}

	public void setEidtQueryCheckboxField(String eidtQueryCheckboxField) {
		this.eidtQueryCheckboxField = eidtQueryCheckboxField;
	}

	public String getEditRow() {
		return editRow;
	}

	public void setEditRow(String editRow) {
		this.editRow = editRow;
	}

	public String getEditRowHidden() {
		return editRowHidden;
	}

	public void setEditRowHidden(String editRowHidden) {
		this.editRowHidden = editRowHidden;
	}

	public String getEditRowCheckbox() {
		return editRowCheckbox;
	}

	public void setEditRowCheckbox(String editRowCheckbox) {
		this.editRowCheckbox = editRowCheckbox;
	}

	public String getEditRowCombo() {
		return editRowCombo;
	}

	public void setEditRowCombo(String editRowCombo) {
		this.editRowCombo = editRowCombo;
	}

	public String getEditRowMulitCheckbox() {
		return editRowMulitCheckbox;
	}

	public void setEditRowMulitCheckbox(String editRowMulitCheckbox) {
		this.editRowMulitCheckbox = editRowMulitCheckbox;
	}

	public String getSearchSql() {
		return searchSql;
	}

	public void setSearchSql(String searchSql) {
		this.searchSql = searchSql;
	}

	public String getSearchQueryGridFieldSql() {
		return searchQueryGridFieldSql;
	}

	public void setSearchQueryGridFieldSql(String searchQueryGridFieldSql) {
		this.searchQueryGridFieldSql = searchQueryGridFieldSql;
	}

	public String getQueryExcelFieldSql() {
		return queryExcelFieldSql;
	}

	public void setQueryExcelFieldSql(String queryExcelFieldSql) {
		this.queryExcelFieldSql = queryExcelFieldSql;
	}

	public String getQueryPdfFieldSql() {
		return queryPdfFieldSql;
	}

	public void setQueryPdfFieldSql(String queryPdfFieldSql) {
		this.queryPdfFieldSql = queryPdfFieldSql;
	}

	public String getQueryFilterField() {
		return queryFilterField;
	}

	public void setQueryFilterField(String queryFilterField) {
		this.queryFilterField = queryFilterField;
	}

	public String getQueryShowField() {
		return queryShowField;
	}

	public void setQueryShowField(String queryShowField) {
		this.queryShowField = queryShowField;
	}
	
	public String gettTextShowonly() {
		return tTextShowonly;
	}

	public void settTextShowonly(String tTextShowonly) {
		this.tTextShowonly = tTextShowonly;
	}

	public String gettTextShowonlyFilter() {
		return tTextShowonlyFilter;
	}

	public void settTextShowonlyFilter(String tTextShowonlyFilter) {
		this.tTextShowonlyFilter = tTextShowonlyFilter;
	}

	public String getShowRow() {
		return showRow;
	}

	public void setShowRow(String showRow) {
		this.showRow = showRow;
	}

	public String getInsertAuditSql() {
		return insertAuditSql;
	}

	public void setInsertAuditSql(String insertAuditSql) {
		this.insertAuditSql = insertAuditSql;
	}
	
	public String getUpdateQuerySnapshotSql() {
		return updateQuerySnapshotSql;
	}

	public void setUpdateQuerySnapshotSql(String updateQuerySnapshotSql) {
		this.updateQuerySnapshotSql = updateQuerySnapshotSql;
	}

	public String gettItemNone() {
		return tItemNone;
	}

	public void settItemNone(String tItemNone) {
		this.tItemNone = tItemNone;
	}

	public String gettItemNoneFilter() {
		return tItemNoneFilter;
	}

	public void settItemNoneFilter(String tItemNoneFilter) {
		this.tItemNoneFilter = tItemNoneFilter;
	}

	public String getQueryDocumentParams() {
		return queryDocumentParams;
	}

	public void setQueryDocumentParams(String queryDocumentParams) {
		this.queryDocumentParams = queryDocumentParams;
	}

	public String getEditQueryDataLog() {
		return editQueryDataLog;
	}

	public void setEditQueryDataLog(String editQueryDataLog) {
		this.editQueryDataLog = editQueryDataLog;
	}

	public String getInsertQueryBpkFieldSql() {
		return insertQueryBpkFieldSql;
	}

	public void setInsertQueryBpkFieldSql(String insertQueryBpkFieldSql) {
		this.insertQueryBpkFieldSql = insertQueryBpkFieldSql;
	}

	public String gettJson() {
		return tJson;
	}

	public void settJson(String tJson) {
		this.tJson = tJson;
	}

	public TemplateBean(){
		
	}
	
	public TemplateBean(ServletContext _ctx) throws Throwable{
    	this._ctx = _ctx;
    	
    	tTh  = getResource("/WEB-INF/action/formgen/pub/template/formgen/view/th.htm");
		tTd  = getResource("/WEB-INF/action/formgen/pub/template/formgen/view/td.htm");
		
    	tRow  = getResource("/WEB-INF/action/formgen/pub/template/formgen/form/row.htm");
	    tItem  = getResource("/WEB-INF/action/formgen/pub/template/formgen/form/item.htm");
	    tItemNone  = getResource("/WEB-INF/action/formgen/pub/template/formgen/form/item-none.htm");
	    tCheckbox = getResource("/WEB-INF/action/formgen/pub/template/formgen/form/checkbox.htm");
	    tRadiobox = getResource("/WEB-INF/action/formgen/pub/template/formgen/form/radio.htm");
	    tText = getResource("/WEB-INF/action/formgen/pub/template/formgen/form/text.htm");
	    tCombo = getResource("/WEB-INF/action/formgen/pub/template/formgen/form/combo.htm");
	    tDate = getResource("/WEB-INF/action/formgen/pub/template/formgen/form/date.htm");
	    tDateTime = getResource("/WEB-INF/action/formgen/pub/template/formgen/form/datetime.htm");
	    tHidden = getResource("/WEB-INF/action/formgen/pub/template/formgen/form/hidden.htm");
	    tTextReadonly = getResource("/WEB-INF/action/formgen/pub/template/formgen/form/text_readonly.htm");
	    tTextShowonly = getResource("/WEB-INF/action/formgen/pub/template/formgen/form/text_showonly.htm");
	    tTextarea = getResource("/WEB-INF/action/formgen/pub/template/formgen/form/textarea.htm");
	    tPickup = getResource("/WEB-INF/action/formgen/pub/template/formgen/form/pickup.htm");
	    tPlugin = getResource("/WEB-INF/action/formgen/pub/template/formgen/form/plugin.htm");
	    tEmpty = getResource("/WEB-INF/action/formgen/pub/template/formgen/form/empty.htm");
	    
	    tRowFilter  = getResource("/WEB-INF/action/formgen/pub/template/formgen/filter/row.htm");
	    tItemFilter  = getResource("/WEB-INF/action/formgen/pub/template/formgen/filter/item.htm");
	    tItemNoneFilter  = getResource("/WEB-INF/action/formgen/pub/template/formgen/filter/item-none.htm");
	    tCheckboxFilter = getResource("/WEB-INF/action/formgen/pub/template/formgen/filter/checkbox.htm");
	    tRadioboxFilter = getResource("/WEB-INF/action/formgen/pub/template/formgen/filter/radio.htm");
	    tTextFilter = getResource("/WEB-INF/action/formgen/pub/template/formgen/filter/text.htm");
	    tComboFilter = getResource("/WEB-INF/action/formgen/pub/template/formgen/filter/combo.htm");
	    tDateFilter = getResource("/WEB-INF/action/formgen/pub/template/formgen/filter/date.htm");
	    tDateTimeFilter = getResource("/WEB-INF/action/formgen/pub/template/formgen/filter/datetime.htm");
	    tHiddenFilter = getResource("/WEB-INF/action/formgen/pub/template/formgen/filter/hidden.htm");
	    tTextReadonlyFilter = getResource("/WEB-INF/action/formgen/pub/template/formgen/filter/text_readonly.htm");
	    tTextShowonlyFilter = getResource("/WEB-INF/action/formgen/pub/template/formgen/filter/text_showonly.htm");
	    tTextareaFilter = getResource("/WEB-INF/action/formgen/pub/template/formgen/filter/textarea.htm");
	    tPickupFilter = getResource("/WEB-INF/action/formgen/pub/template/formgen/filter/pickup.htm");
	    tPluginFilter = getResource("/WEB-INF/action/formgen/pub/template/formgen/filter/plugin.htm");
	    tEmptyFilter = getResource("/WEB-INF/action/formgen/pub/template/formgen/filter/empty.htm");
	    
	    strDomainCn = getResource("/WEB-INF/action/formgen/pub/template/formgen/sql/query_domain_cn.sql");
	    strDomainEn = getResource("/WEB-INF/action/formgen/pub/template/formgen/sql/query_domain_en.sql");
	    strFkField = getResource("/WEB-INF/action/formgen/pub/template/formgen/sql/query_fk_field.sql");
	    strFkDataCn = getResource("/WEB-INF/action/formgen/pub/template/formgen/sql/query_fk_data_cn.sql");
	    strFkDataEn = getResource("/WEB-INF/action/formgen/pub/template/formgen/sql/query_fk_data_en.sql");
	    
	    queryForm = getResource("/WEB-INF/action/formgen/pub/template/formgen/sql/query_form.sql");
	    queryDocument = getResource("/WEB-INF/action/formgen/pub/template/formgen/sql/query_document.sql");
	    queryDocumentParams = getResource("/WEB-INF/action/formgen/pub/template/formgen/sql/query_document_params.sql");
	    queryItem = getResource("/WEB-INF/action/formgen/pub/template/formgen/sql/query_item.sql");
	    formQueryFieldSql = getResource("/WEB-INF/action/formgen/pub/template/formgen/sql/form_query_field.sql");
	    queryCascadeSql = getResource("/WEB-INF/action/formgen/pub/template/formgen/sql/query_cascade_field.sql");
	    filterQueryFieldSql = getResource("/WEB-INF/action/formgen/pub/template/formgen/sql/filter_query_field.sql");
	    viewQueryGridField = getResource("/WEB-INF/action/formgen/pub/template/formgen/sql/view_query_grid_field.sql");
	    deleteSql = getResource("/WEB-INF/action/formgen/pub/template/formgen/sql/delete_sql.sql");
	    deleteUpdateSql = getResource("/WEB-INF/action/formgen/pub/template/formgen/sql/delete_update_sql.sql");
	    insertSql = getResource("/WEB-INF/action/formgen/pub/template/formgen/sql/insert_sql.sql");
	    updateSql = getResource("/WEB-INF/action/formgen/pub/template/formgen/sql/update_sql.sql");
	    insertQueryFieldSql = getResource("/WEB-INF/action/formgen/pub/template/formgen/sql/insert_query_field.sql");
	    insertQueryBpkFieldSql = getResource("/WEB-INF/action/formgen/pub/template/formgen/sql/insert_query_bpk_field.sql");
	    updateQueryFieldSql = getResource("/WEB-INF/action/formgen/pub/template/formgen/sql/update_query_field.sql");
	    updateQuerySnapshotSql = getResource("/WEB-INF/action/formgen/pub/template/formgen/sql/update_query_snapshot.sql");
	    editSql = getResource("/WEB-INF/action/formgen/pub/template/formgen/sql/edit_sql.sql");
	    editQueryComboField = getResource("/WEB-INF/action/formgen/pub/template/formgen/sql/edit_query_combo.sql");
	    editQueryShowField = getResource("/WEB-INF/action/formgen/pub/template/formgen/sql/edit_query_show.sql");
	    eidtQueryCheckboxField = getResource("/WEB-INF/action/formgen/pub/template/formgen/sql/edit_query_checkbox.sql");
	    editQueryField = getResource("/WEB-INF/action/formgen/pub/template/formgen/sql/edit_query_field.sql");
	    editQueryDataLog = getResource("/WEB-INF/action/formgen/pub/template/formgen/sql/edit_query_table_data_log.sql");
	    searchSql = getResource("/WEB-INF/action/formgen/pub/template/formgen/sql/search_query-base.sql");
	    searchQueryGridFieldSql = getResource("/WEB-INF/action/formgen/pub/template/formgen/sql/search_query_grid_field.sql");
	    queryExcelFieldSql = getResource("/WEB-INF/action/formgen/pub/template/formgen/sql/query_excel_field.sql");
	    queryPdfFieldSql = getResource("/WEB-INF/action/formgen/pub/template/formgen/sql/query_pdf_field.sql");
	    queryFilterField = getResource("/WEB-INF/action/formgen/pub/template/formgen/sql/query_filter_field.sql");
	    queryShowField = getResource("/WEB-INF/action/formgen/pub/template/formgen/sql/query_show_field.sql");
	    
	    insertAuditSql = getResource("/WEB-INF/action/formgen/pub/template/formgen/sql/insert_audit_sql.sql");

	    editRow = getResource("/WEB-INF/action/formgen/pub/template/formgen/edit/rows.js");
	    editRowHidden = getResource("/WEB-INF/action/formgen/pub/template/formgen/edit/rows-hidden.js");
	    editRowCheckbox = getResource("/WEB-INF/action/formgen/pub/template/formgen/edit/rows-checkbox.js");
	    editRowCombo = getResource("/WEB-INF/action/formgen/pub/template/formgen/edit/rows-combo.js");
	    editRowMulitCheckbox = getResource("/WEB-INF/action/formgen/pub/template/formgen/edit/rows-mulit-checkbox.js");
	    showRow = getResource("/WEB-INF/action/formgen/pub/template/formgen/edit/rows-show.js");
    }

    public String getResource(String fileName) throws Throwable
	{
	    String path = fileName;
	    String encoding = _ctx.getInitParameter("file-encoding");
	    if(encoding != null && encoding.trim().equals(""))
	        encoding = null;
	    if(encoding != null)
	        return StringUtil.getResource(_ctx, path, encoding);
	    else
	        return StringUtil.getResource(_ctx, path);
	}
}
