package com.ccms.core.formengine;

import java.io.Serializable;

import javax.servlet.ServletContext;

import com.ccms.core.foctory.TemplateBean;

/**
 * @author zhangchuan
 * 存储模板文件
 *
 */
public class FormTemplateEngine extends TemplateBean implements Serializable {

	private static final long serialVersionUID = 6144106967195239378L;

	public FormTemplateEngine(ServletContext _ctx) throws Throwable{
    	this._ctx = _ctx;
    	
    	tTh  = getResource("/WEB-INF/action/ccms/formgen/template/view/th.htm");
		tTd  = getResource("/WEB-INF/action/ccms/formgen/template/view/td.htm");
		tJson  = getResource("/WEB-INF/action/ccms/formgen/template/view/json.htm");
		
    	tRow  = getResource("/WEB-INF/action/ccms/formgen/template/form/row.htm");
	    tItem  = getResource("/WEB-INF/action/ccms/formgen/template/form/item.htm");
	    tItemNone  = getResource("/WEB-INF/action/ccms/formgen/template/form/item-none.htm");
	    tCheckbox = getResource("/WEB-INF/action/ccms/formgen/template/form/checkbox.htm");
	    tRadiobox = getResource("/WEB-INF/action/ccms/formgen/template/form/radio.htm");
	    tText = getResource("/WEB-INF/action/ccms/formgen/template/form/text.htm");
	    tCombo = getResource("/WEB-INF/action/ccms/formgen/template/form/combo.htm");
	    tDate = getResource("/WEB-INF/action/ccms/formgen/template/form/date.htm");
	    tDateTime = getResource("/WEB-INF/action/ccms/formgen/template/form/datetime.htm");
	    tHidden = getResource("/WEB-INF/action/ccms/formgen/template/form/hidden.htm");
	    tTextReadonly = getResource("/WEB-INF/action/ccms/formgen/template/form/text_readonly.htm");
	    tTextShowonly = getResource("/WEB-INF/action/ccms/formgen/template/form/text_showonly.htm");
	    tTextarea = getResource("/WEB-INF/action/ccms/formgen/template/form/textarea.htm");
	    tPickup = getResource("/WEB-INF/action/ccms/formgen/template/form/pickup.htm");
	    tPlugin = getResource("/WEB-INF/action/ccms/formgen/template/form/plugin.htm");
	    tEmpty = getResource("/WEB-INF/action/ccms/formgen/template/form/empty.htm");
	    
	    tRowFilter  = getResource("/WEB-INF/action/ccms/formgen/template/filter/row.htm");
	    tItemFilter  = getResource("/WEB-INF/action/ccms/formgen/template/filter/item.htm");
	    tItemNoneFilter  = getResource("/WEB-INF/action/ccms/formgen/template/filter/item-none.htm");
	    tCheckboxFilter = getResource("/WEB-INF/action/ccms/formgen/template/filter/checkbox.htm");
	    tRadioboxFilter = getResource("/WEB-INF/action/ccms/formgen/template/filter/radio.htm");
	    tTextFilter = getResource("/WEB-INF/action/ccms/formgen/template/filter/text.htm");
	    tComboFilter = getResource("/WEB-INF/action/ccms/formgen/template/filter/combo.htm");
	    tDateFilter = getResource("/WEB-INF/action/ccms/formgen/template/filter/date.htm");
	    tDateTimeFilter = getResource("/WEB-INF/action/ccms/formgen/template/filter/datetime.htm");
	    tHiddenFilter = getResource("/WEB-INF/action/ccms/formgen/template/filter/hidden.htm");
	    tTextReadonlyFilter = getResource("/WEB-INF/action/ccms/formgen/template/filter/text_readonly.htm");
	    tTextShowonlyFilter = getResource("/WEB-INF/action/ccms/formgen/template/filter/text_showonly.htm");
	    tTextareaFilter = getResource("/WEB-INF/action/ccms/formgen/template/filter/textarea.htm");
	    tPickupFilter = getResource("/WEB-INF/action/ccms/formgen/template/filter/pickup.htm");
	    tPluginFilter = getResource("/WEB-INF/action/ccms/formgen/template/filter/plugin.htm");
	    tEmptyFilter = getResource("/WEB-INF/action/ccms/formgen/template/filter/empty.htm");
	    
	    strDomainCn = getResource("/WEB-INF/action/ccms/formgen/template/sql/query_domain_cn.sql");
	    strDomainEn = getResource("/WEB-INF/action/ccms/formgen/template/sql/query_domain_en.sql");
	    strFkField = getResource("/WEB-INF/action/ccms/formgen/template/sql/query_fk_field.sql");
	    strFkDataCn = getResource("/WEB-INF/action/ccms/formgen/template/sql/query_fk_data_cn.sql");
	    strFkDataEn = getResource("/WEB-INF/action/ccms/formgen/template/sql/query_fk_data_en.sql");
	    
	    queryForm = getResource("/WEB-INF/action/ccms/formgen/template/sql/query_form.sql");
	    queryDocument = getResource("/WEB-INF/action/ccms/formgen/template/sql/query_document.sql");
	    queryDocumentParams = getResource("/WEB-INF/action/ccms/formgen/template/sql/query_document_params.sql");
	    queryItem = getResource("/WEB-INF/action/ccms/formgen/template/sql/query_item.sql");
	    formQueryFieldSql = getResource("/WEB-INF/action/ccms/formgen/template/sql/form_query_field.sql");
	    queryCascadeSql = getResource("/WEB-INF/action/ccms/formgen/template/sql/query_cascade_field.sql");
	    filterQueryFieldSql = getResource("/WEB-INF/action/ccms/formgen/template/sql/filter_query_field.sql");
	    viewQueryGridField = getResource("/WEB-INF/action/ccms/formgen/template/sql/view_query_grid_field.sql");
	    deleteSql = getResource("/WEB-INF/action/ccms/formgen/template/sql/delete_sql.sql");
	    deleteUpdateSql = getResource("/WEB-INF/action/ccms/formgen/template/sql/delete_update_sql.sql");
	    insertSql = getResource("/WEB-INF/action/ccms/formgen/template/sql/insert_sql.sql");
	    updateSql = getResource("/WEB-INF/action/ccms/formgen/template/sql/update_sql.sql");
	    insertQueryFieldSql = getResource("/WEB-INF/action/ccms/formgen/template/sql/insert_query_field.sql");
	    insertQueryBpkFieldSql = getResource("/WEB-INF/action/ccms/formgen/template/sql/insert_query_bpk_field.sql");
	    updateQueryFieldSql = getResource("/WEB-INF/action/ccms/formgen/template/sql/update_query_field.sql");
	    updateQuerySnapshotSql = getResource("/WEB-INF/action/ccms/formgen/template/sql/update_query_snapshot.sql");
	    editSql = getResource("/WEB-INF/action/ccms/formgen/template/sql/edit_sql.sql");
	    editQueryComboField = getResource("/WEB-INF/action/ccms/formgen/template/sql/edit_query_combo.sql");
	    editQueryShowField = getResource("/WEB-INF/action/ccms/formgen/template/sql/edit_query_show.sql");
	    eidtQueryCheckboxField = getResource("/WEB-INF/action/ccms/formgen/template/sql/edit_query_checkbox.sql");
	    editQueryField = getResource("/WEB-INF/action/ccms/formgen/template/sql/edit_query_field.sql");
	    editQueryDataLog = getResource("/WEB-INF/action/ccms/formgen/template/sql/edit_query_table_data_log.sql");
	    searchSql = getResource("/WEB-INF/action/ccms/formgen/template/sql/search_query-base.sql");
	    searchQueryGridFieldSql = getResource("/WEB-INF/action/ccms/formgen/template/sql/search_query_grid_field.sql");
	    queryExcelFieldSql = getResource("/WEB-INF/action/ccms/formgen/template/sql/query_excel_field.sql");
	    queryPdfFieldSql = getResource("/WEB-INF/action/ccms/formgen/template/sql/query_pdf_field.sql");
	    queryFilterField = getResource("/WEB-INF/action/ccms/formgen/template/sql/query_filter_field.sql");
	    queryShowField = getResource("/WEB-INF/action/ccms/formgen/template/sql/query_show_field.sql");
	    
	    insertAuditSql = getResource("/WEB-INF/action/ccms/formgen/template/sql/insert_audit_sql.sql");

	    editRow = getResource("/WEB-INF/action/ccms/formgen/template/edit/rows.js");
	    editRowHidden = getResource("/WEB-INF/action/ccms/formgen/template/edit/rows-hidden.js");
	    editRowCheckbox = getResource("/WEB-INF/action/ccms/formgen/template/edit/rows-checkbox.js");
	    editRowCombo = getResource("/WEB-INF/action/ccms/formgen/template/edit/rows-combo.js");
	    editRowMulitCheckbox = getResource("/WEB-INF/action/ccms/formgen/template/edit/rows-mulit-checkbox.js");
	    showRow = getResource("/WEB-INF/action/ccms/formgen/template/edit/rows-show.js");
    }
}
