package com.ccms.core.foctory;

import java.util.Map;

import com.ccms.context.InitializerServlet;

import dinamica.Db;
import dinamica.Recordset;
import dinamica.StringUtil;
import dinamica.TemplateEngine;
import dinamica.security.DinamicaUser;

/**
 * @author zhangchuan
 * 表单缓存管理
 *
 */
public class FormFactory {

	private Db db = null;
	/**
	 * 初始化Factory时需要给db赋值，调用结束时要记得关闭连接
	 * @return
	 */
	public Db getDb() {
		return db;
	}

	public void setDb(Db db) {
		this.db = db;
	}

	public FormBean loadForm(Integer formId, TemplateBean tmp) throws Throwable{
		FormBean form = getFormById(formId, tmp);
		
		//加载form 界面
		loadFormFormPage(form, tmp);
		
		//加载查询条件界面
		loadFormFilterPage(form, tmp);
		
		//加载查询结果界面
		loadFormViewPage(form, tmp);
		
		//加载删除语句
		loadDeleteSql(form, tmp);
		
		//加载新增语句
		loadInsertSql(form, tmp);
		
		//加载修改语句
		loadUpdateSql(form, tmp);
		
		//加载审计语句
		loadAuditSql(form, tmp);
		
		//加载编辑语句和脚本
		loadEditSqlScript(form, tmp);
		//话术显示脚本
		loadEditSqlItemScript(form, tmp);
		
		//加载查询语句
		loadSearchSql(form, tmp);
		
		//加载excel导出字段
		loadExcelField(form, tmp);
		
		//加载pdf导出字段
		loadPdfField(form, tmp);
		
		//加载查询条件和表单显示字段（入口进来时传递值，给查询条件和界面字段赋值）
		loadFilterShowField(form, tmp);
		
		//加载表单权限
		loadOperPriviledge(form);
		
		return form;
	}
	
	public FormBean getFormById(Integer formId, TemplateBean tmp) throws Throwable{
		String queryForm = tmp.getQueryForm();
		queryForm = StringUtil.replace(queryForm, "${form_id}", formId.toString());
		Recordset rsForm = getDb().get(queryForm);
		rsForm.first();
		FormBean form = new FormBean(rsForm);
		return form;
	}
	
	public void loadFormFormPage(FormBean bean, TemplateBean tmp) throws Throwable{
		if(tmp == null){
			tmp = new TemplateBean(InitializerServlet.getContext());
		}
		FormOutput fop = new FormOutput(getDb());
		String formControlsCn = fop.printPage(bean, tmp, 1, 1);
		String formControlsEn = fop.printPage(bean, tmp, 1, 2);
		Map<Integer, FormItemBean> itemMap = fop.printFormItemPage(bean, tmp, 1, 1);

		//加载级联字段
		String queryCascadeSql = tmp.getQueryCascadeSql();
		queryCascadeSql = StringUtil.replace(queryCascadeSql, "${form_id}", bean.getForm_id().toString());
		Recordset rsField = getDb().get(queryCascadeSql);

		//替换 DEF 标记
		formControlsCn = StringUtil.replace(formControlsCn, "${DEF", "${def");
		formControlsEn = StringUtil.replace(formControlsEn, "${DEF", "${def");
		
		//替换LBL 标记
		formControlsCn = StringUtil.replace(formControlsCn, "${LBL", "${lbl");
		formControlsEn = StringUtil.replace(formControlsEn, "${LBL", "${lbl");
		
		//返回值赋回form
		bean.setFormControlsCn(formControlsCn);
		bean.setFormControlsEn(formControlsEn);
		bean.setQueryCascadeField(rsField);
		bean.setItemMap(itemMap);
	}
	
	public void loadFormFilterPage(FormBean bean, TemplateBean tmp) throws Throwable{
		if(tmp == null){
			tmp = new TemplateBean(InitializerServlet.getContext());
		}
		FormOutput fop = new FormOutput(getDb());
		String filterControlsCn = fop.printPage(bean, tmp, 2, 1);
		String filterControlsEn = fop.printPage(bean, tmp, 2, 2);
		
		//替换 DEF 标记
		filterControlsCn = StringUtil.replace(filterControlsCn, "${DEF", "${def");
		filterControlsEn = StringUtil.replace(filterControlsEn, "${DEF", "${def");
		
		//返回值赋回form
		bean.setFilterControlsCn(filterControlsCn);
		bean.setFilterControlsEn(filterControlsEn);
	}
	
	public void loadFormViewPage(FormBean bean, TemplateBean tmp) throws Throwable{
		if(tmp == null){
			tmp = new TemplateBean(InitializerServlet.getContext());
		}
		String viewQueryGridField = tmp.getViewQueryGridField();
		viewQueryGridField = StringUtil.replace(viewQueryGridField, "${form_id}", bean.getForm_id().toString());
		Recordset rsField = getDb().get(viewQueryGridField);
		StringBuilder sTitleCn = new StringBuilder();
		StringBuilder sTitleEn = new StringBuilder();
		StringBuilder sField = new StringBuilder();
		rsField.top();
		while(rsField.next()){
			//设置中文
			rsField.setValue("field_name", rsField.getValue("field_name_cn"));
			TemplateEngine thFieldCn = new TemplateEngine(null, null, tmp.gettTh());
			thFieldCn.replace(rsField,"");
			sTitleCn.append(thFieldCn.toString());
			
			//设置英文
			rsField.setValue("field_name", rsField.getValue("field_name_en"));
			TemplateEngine thFieldEn = new TemplateEngine(null, null, tmp.gettTh());
			thFieldEn.replace(rsField,"");
			sTitleEn.append(thFieldEn.toString());
			
			//输出字段
			TemplateEngine tdField = new TemplateEngine(null, null, tmp.gettTd());
			tdField.replace(rsField,"");
			sField.append(tdField.toString());
		}
		//返回值赋回form
		bean.setViewField(sField.toString());
		bean.setViewTitleCn(sTitleCn.toString());
		bean.setViewTitleEn(sTitleEn.toString());
		//用于汇总列
		bean.setViewQueryGridField(rsField);
	}
	
	public void loadDeleteSql(FormBean bean, TemplateBean tmp) throws Throwable{
		if(tmp == null){
			tmp = new TemplateBean(InitializerServlet.getContext());
		}
		String delete_field = bean.getDelete_field();
		String delete = tmp.getDeleteSql();
		String deleteUpdate = tmp.getDeleteUpdateSql();
		if(delete_field==null || delete_field.equals("")){
			delete = StringUtil.replace(delete, "${table}", bean.getTable_code());
			delete = StringUtil.replace(delete, "${bpk_field}", bean.getBpk_field());
		}else{
			deleteUpdate = StringUtil.replace(deleteUpdate, "${table}", bean.getTable_code());
			deleteUpdate = StringUtil.replace(deleteUpdate, "${bpk_field}", bean.getBpk_field());
			deleteUpdate = StringUtil.replace(deleteUpdate, "${delete_field}", delete_field);
			delete = deleteUpdate;
		}
		//返回值赋回form
		bean.setDelete_sql(delete);
	}
	
	public void loadInsertSql(FormBean bean, TemplateBean tmp) throws Throwable{
		if(tmp == null){
			tmp = new TemplateBean(InitializerServlet.getContext());
		}
		String insertQueryField = tmp.getInsertQueryFieldSql();
		insertQueryField = StringUtil.replace(insertQueryField, "${form_id}", bean.getForm_id().toString());
		Recordset rsField = getDb().get(insertQueryField);
		
		StringBuilder insertField = new StringBuilder();
		StringBuilder insertMark = new StringBuilder();
		rsField.top();
		while(rsField.next()){
			String is_virtual_type = rsField.getString("is_virtual_type");
			if ("0".equals(is_virtual_type)){
				insertField.append(rsField.getString("field_code")).append(",");
				insertMark.append(rsField.getString("colname_mark")).append(",");
			}
		}
		String insert = tmp.getInsertSql();
		insert = StringUtil.replace(insert, "${table}", bean.getTable_code());
		insert = StringUtil.replace(insert, "${bpk_field}", bean.getBpk_field());

		if(!"integer".equalsIgnoreCase(bean.getBpk_field_type())){
			insert = StringUtil.replace(insert, "${bpk_field_value}", "'${bpk_field_value}'");
		}

		insert = StringUtil.replace(insert, "${field}", insertField.toString());
		insert = StringUtil.replace(insert, "${field_mark}", insertMark.toString());
		
		String insertQueryBpkFieldSql = tmp.getInsertQueryBpkFieldSql();
		insertQueryBpkFieldSql = StringUtil.replace(insertQueryBpkFieldSql, "${bpk_field_prefix}", bean.getBpk_field_prefix()==null?"":bean.getBpk_field_prefix());
		insertQueryBpkFieldSql = StringUtil.replace(insertQueryBpkFieldSql, "${bpk_field_seq}", bean.getBpk_field_seq());
		
		//返回值赋回form
		bean.setInsert_sql(insert);
		bean.setInsertQueryField(rsField);
		bean.setInsert_query_bpk_field_sql(insertQueryBpkFieldSql);
	}
	
	public void loadUpdateSql(FormBean bean, TemplateBean tmp) throws Throwable{
		if(tmp == null){
			tmp = new TemplateBean(InitializerServlet.getContext());
		}
		String updateQueryField = tmp.getUpdateQueryFieldSql();
		updateQueryField = StringUtil.replace(updateQueryField, "${form_id}", bean.getForm_id().toString());
		Recordset rsField = getDb().get(updateQueryField);
		
		StringBuilder updateSb = new StringBuilder();
		rsField.top();
		while(rsField.next()){
			String is_virtual_type = rsField.getString("is_virtual_type");
			if ("0".equals(is_virtual_type)){
				updateSb.append(rsField.getString("field_code")).append("=").append(rsField.getString("colname_mark")).append(",");
			}
		}
		
		if (updateSb.length()>0&&updateSb.charAt(updateSb.length()-1) ==','){
			updateSb.delete(updateSb.length()-1, updateSb.length());
		}
		
		String update = tmp.getUpdateSql();
		update = StringUtil.replace(update, "${table}", bean.getTable_code());
		update = StringUtil.replace(update, "${bpk_field}", bean.getBpk_field());
		update = StringUtil.replace(update, "${field}", updateSb.toString());
		
		String querySnapshot = tmp.getUpdateQuerySnapshotSql();
		querySnapshot = StringUtil.replace(querySnapshot, "${table}", bean.getTable_code());
		querySnapshot = StringUtil.replace(querySnapshot, "${bpk_field}", bean.getBpk_field());
		
		//返回值赋回form
		bean.setUpdate_sql(update);
		bean.setUpdateQueryField(rsField);
		bean.setQuery_snapshot_sql(querySnapshot);
	}
	
	public void loadEditSqlScript(FormBean bean, TemplateBean tmp) throws Throwable{
		if(tmp == null){
			tmp = new TemplateBean(InitializerServlet.getContext());
		}
		String editQueryField = tmp.getEditQueryField();
		editQueryField = StringUtil.replace(editQueryField, "${form_id}", bean.getForm_id().toString());
		Recordset rsField = getDb().get(editQueryField);
		
		StringBuilder editSb = new StringBuilder();
		rsField.top();
		while(rsField.next()){
			editSb.append(rsField.getString("field_code")).append(" as ").append(rsField.getString("colname")).append(",");
		}
		/*TOBE CONFIRM*/
		if (editSb.length()>0&&editSb.charAt(editSb.length()-1) ==','){
			editSb.delete(editSb.length()-1, editSb.length());
		}
		String edit = tmp.getEditSql();
		String edit_sql = bean.getEdit_sql();
		if(edit_sql != null && edit_sql.trim().length() > 0){//替换现有的查询语句
			if(edit_sql.indexOf("${DEF") >= 0){
				edit_sql = StringUtil.replace(edit_sql, "${DEF", "${def");
			}
			edit = StringUtil.replace(edit, "${table}", edit_sql);
		}
		
		edit = StringUtil.replace(edit, "${table}", bean.getTable_code());
		edit = StringUtil.replace(edit, "${bpk_field}", bean.getBpk_field());
		edit = StringUtil.replace(edit, "${field}", editSb.toString());
		
		//去除item定义
		String row = StringUtil.replace(tmp.getEditRow(),"_${fld:form_item_id}","");;
		String rowHidden = StringUtil.replace(tmp.getEditRowHidden(),"_${fld:form_item_id}","");;
		String rowCheckbox = StringUtil.replace(tmp.getEditRowCheckbox(),"_${fld:form_item_id}","");;
		String rowCombo = StringUtil.replace(tmp.getEditRowCombo(),"_${fld:form_item_id}","");;
		String rowMulitCheckbox = StringUtil.replace(tmp.getEditRowMulitCheckbox(),"_${fld:form_item_id}","");;
		String rowShow = StringUtil.replace(tmp.getShowRow(),"_${fld:form_item_id}","");;
		
		String editQueryCheckbboxField = tmp.getEidtQueryCheckboxField();
		editQueryCheckbboxField = StringUtil.replace(editQueryCheckbboxField, "${form_id}", bean.getForm_id().toString());
		Recordset rsCheckboxField = getDb().get(editQueryCheckbboxField);
		String editQueryComboField = tmp.getEditQueryComboField();
		editQueryComboField = StringUtil.replace(editQueryComboField, "${form_id}", bean.getForm_id().toString());
		Recordset rsComboField = getDb().get(editQueryComboField);

		String editQueryShowField = tmp.getEditQueryShowField();
		editQueryShowField = StringUtil.replace(editQueryShowField, "${form_id}", bean.getForm_id().toString());
		Recordset rsShowField = getDb().get(editQueryShowField);
		
		StringBuilder editExceJs = new StringBuilder();
		
		TemplateEngine tRow = new TemplateEngine(null,null, row);
		tRow.replace(rsField,"","rows");
		editExceJs.append(tRow.toString());
		
		TemplateEngine tRowHidden = new TemplateEngine(null,null, rowHidden);
		tRowHidden.replace(rsField,"","rows");
		editExceJs.append(tRowHidden.toString());
		
		TemplateEngine tRowCombo = new TemplateEngine(null,null, rowCombo);
		tRowCombo.replace(rsComboField,"","rows");
		editExceJs.append(tRowCombo.toString());

		TemplateEngine tRowShow = new TemplateEngine(null,null, rowShow);
		tRowShow.replace(rsShowField,"","rows");
		editExceJs.append(tRowShow.toString());
		
		TemplateEngine tRowCheckbox = new TemplateEngine(null,null, rowCheckbox);
		tRowCheckbox.replace(rsCheckboxField,"","rows");
		editExceJs.append(tRowCheckbox.toString());
		
		TemplateEngine tRowMulitCheckbox = new TemplateEngine(null,null, rowMulitCheckbox);
		tRowMulitCheckbox.replace(rsCheckboxField,"","rows");
		editExceJs.append(tRowMulitCheckbox.toString());
		
		//版本还原时语句
		String queryDataLog = tmp.getEditQueryDataLog();
		queryDataLog = StringUtil.replace(queryDataLog, "${table}", bean.getTable_code());
		
		//返回值赋回form
		bean.setEdit_sql(edit);
		bean.setEdit_exce_js(editExceJs.toString());
		bean.setEdit_query_data_log(queryDataLog);
	}

	public void loadEditSqlItemScript(FormBean bean, TemplateBean tmp) throws Throwable{
		if(tmp == null){
			tmp = new TemplateBean(InitializerServlet.getContext());
		}
		String editQueryField = tmp.getEditQueryField();
		editQueryField = StringUtil.replace(editQueryField, "${form_id}", bean.getForm_id().toString());
		Recordset rsField = getDb().get(editQueryField);
		
		StringBuilder editSb = new StringBuilder();
		rsField.top();
		while(rsField.next()){
			editSb.append(rsField.getString("field_code")).append(" as ").append(rsField.getString("colname")).append(",");
		}
		/*TOBE CONFIRM*/
		if (editSb.length()>0&&editSb.charAt(editSb.length()-1) ==','){
			editSb.delete(editSb.length()-1, editSb.length());
		}
		
		String row = tmp.getEditRow();
		String rowHidden = tmp.getEditRowHidden();
		String rowCheckbox = tmp.getEditRowCheckbox();
		String rowCombo = tmp.getEditRowCombo();
		String rowMulitCheckbox = tmp.getEditRowMulitCheckbox();
		String rowShow = tmp.getShowRow();
		
		String editQueryCheckbboxField = tmp.getEidtQueryCheckboxField();
		editQueryCheckbboxField = StringUtil.replace(editQueryCheckbboxField, "${form_id}", bean.getForm_id().toString());
		Recordset rsCheckboxField = getDb().get(editQueryCheckbboxField);
		String editQueryComboField = tmp.getEditQueryComboField();
		editQueryComboField = StringUtil.replace(editQueryComboField, "${form_id}", bean.getForm_id().toString());
		Recordset rsComboField = getDb().get(editQueryComboField);

		String editQueryShowField = tmp.getEditQueryShowField();
		editQueryShowField = StringUtil.replace(editQueryShowField, "${form_id}", bean.getForm_id().toString());
		Recordset rsShowField = getDb().get(editQueryShowField);
		
		StringBuilder editExceJs = new StringBuilder();
		
		TemplateEngine tRow = new TemplateEngine(null,null, row);
		tRow.replace(rsField,"","rows");
		editExceJs.append(tRow.toString());
		
		TemplateEngine tRowHidden = new TemplateEngine(null,null, rowHidden);
		tRowHidden.replace(rsField,"","rows");
		editExceJs.append(tRowHidden.toString());
		
		TemplateEngine tRowCombo = new TemplateEngine(null,null, rowCombo);
		tRowCombo.replace(rsComboField,"","rows");
		editExceJs.append(tRowCombo.toString());

		TemplateEngine tRowShow = new TemplateEngine(null,null, rowShow);
		tRowShow.replace(rsShowField,"","rows");
		editExceJs.append(tRowShow.toString());
		
		TemplateEngine tRowCheckbox = new TemplateEngine(null,null, rowCheckbox);
		tRowCheckbox.replace(rsCheckboxField,"","rows");
		editExceJs.append(tRowCheckbox.toString());
		
		TemplateEngine tRowMulitCheckbox = new TemplateEngine(null,null, rowMulitCheckbox);
		tRowMulitCheckbox.replace(rsCheckboxField,"","rows");
		editExceJs.append(tRowMulitCheckbox.toString());
		
		//返回值赋回form
		bean.setEdit_exce_item_js(editExceJs.toString());
	}
	
	public void loadSearchSql(FormBean bean, TemplateBean tmp) throws Throwable{
		if(tmp == null){
			tmp = new TemplateBean(InitializerServlet.getContext());
		}
		String searchQueryField = tmp.getSearchQueryGridFieldSql();
		searchQueryField = StringUtil.replace(searchQueryField, "${form_id}", bean.getForm_id().toString());
		Recordset rsField = getDb().get(searchQueryField);
		//构造合计结果集
		Recordset rsGridTotal = new Recordset();
		rsField.top();
		while(rsField.next()){
			if("1".equals(rsField.getString("compute_total"))){
				rsGridTotal.append(rsField.getString("colname"), java.sql.Types.DOUBLE);
			}
		}
		//如果设置了列求合
		if(rsGridTotal.getFieldCount()>0){
			rsGridTotal.addNew();
			bean.setSearchQueryTotal(rsGridTotal);
		}
		
		//构造检索语句
		StringBuilder searchSbCn = new StringBuilder();
		StringBuilder searchSbEn = new StringBuilder();
		StringBuilder orderbySbCn = new StringBuilder();
		StringBuilder orderbySbEn = new StringBuilder();
		boolean orderbyFlag = false;
		rsField.top();
		while(rsField.next()){
			searchSbCn.append(rsField.getString("field_code_cn")).append(" as ").append(rsField.getString("colname")).append(",");
			searchSbEn.append(rsField.getString("field_code_en")).append(" as ").append(rsField.getString("colname")).append(",");
			
			String sort_order = rsField.getString("sort_order");
			if(sort_order != null && !"".equals(sort_order)){
				orderbyFlag = true;
				orderbySbCn.append(rsField.getString("field_code_cn")).append(" ").append(sort_order).append(",");
				orderbySbEn.append(rsField.getString("field_code_en")).append(" ").append(sort_order).append(",");
			}
		}
		if (searchSbCn.length()>0&&searchSbCn.charAt(searchSbCn.length()-1) ==','){
			searchSbCn.delete(searchSbCn.length()-1, searchSbCn.length());
		}
		if (searchSbEn.length()>0&&searchSbEn.charAt(searchSbEn.length()-1) ==','){
			searchSbEn.delete(searchSbEn.length()-1, searchSbEn.length());
		}
		if(orderbyFlag == true){
			orderbySbCn.insert(0, " order by ");
			orderbySbEn.insert(0, " order by ");
			if (orderbySbCn.charAt(orderbySbCn.length()-1) ==','){
				orderbySbCn.delete(orderbySbCn.length()-1, orderbySbCn.length());
			}
			if (orderbySbEn.charAt(orderbySbEn.length()-1) ==','){
				orderbySbEn.delete(orderbySbEn.length()-1, orderbySbEn.length());
			}
		}
		
		String search = tmp.getSearchSql();
		String search_sql = bean.getSearch_sql();
		if(search_sql != null && search_sql.trim().length() > 0){//替换现有的查询语句
			if(search_sql.indexOf("${DEF") >= 0){
				search_sql = StringUtil.replace(search_sql, "${DEF", "${def");
			}
			search = StringUtil.replace(search,"${table}",search_sql);
		}
		
		search = StringUtil.replace(search, "${table}", bean.getTable_code());
		search = StringUtil.replace(search, "${delete_field}", bean.getDelete_field());
		search = StringUtil.replace(search, "${table_id}", bean.getTable_id().toString());
		search = StringUtil.replace(search, "${bpk_field}", bean.getBpk_field());
		
		//统计总条数的语句（从第一个select到第一个from 之间替换成 "count(1) as record_count"）
		String countSql = StringUtil.replace(search, "${orderby}", "");
		countSql = StringUtil.replace(countSql, "${field}", " count(1) as record_count ");
		
		String searchCn = StringUtil.replace(search, "${field}", searchSbCn.toString());
		String searchEn = StringUtil.replace(search, "${field}", searchSbEn.toString());
		
		//返回值赋回form
		bean.setSearch_sql_cn(searchCn);
		bean.setSearch_sql_en(searchEn);
		bean.setSearch_count_sql(countSql);
		//不再预先设置orderby的值,因为界面可以动态指定 2013-04-23 oasahi
		//由于MSSQL中的分页必须有排序字段，而且为单独处理在排序字段，此处需要把表名替换掉
		String dbName = InitializerServlet.getContext().getInitParameter("db");
		if("sqlserver".equalsIgnoreCase(dbName)){
			bean.setSearch_orderby_cn(StringUtil.replace(orderbySbCn.toString(), bean.getTable_code()+".", ""));
			bean.setSearch_orderby_en(StringUtil.replace(orderbySbEn.toString(), bean.getTable_code()+".", ""));
		}else{
			bean.setSearch_orderby_cn(orderbySbCn.toString());
			bean.setSearch_orderby_en(orderbySbEn.toString());
		}
	}
	
	public void loadExcelField(FormBean bean, TemplateBean tmp) throws Throwable{
		if(tmp == null){
			tmp = new TemplateBean(InitializerServlet.getContext());
		}
		String queryExcelFieldSql = tmp.getQueryExcelFieldSql();
		queryExcelFieldSql = StringUtil.replace(queryExcelFieldSql, "${form_id}", bean.getForm_id().toString());
		Recordset rsField = getDb().get(queryExcelFieldSql);
		
		//返回值赋回form
		bean.setQueryExcelField(rsField);
	}
	
	public void loadPdfField(FormBean bean, TemplateBean tmp) throws Throwable{
		if(tmp == null){
			tmp = new TemplateBean(InitializerServlet.getContext());
		}
		String queryPdfFieldSql = tmp.getQueryPdfFieldSql();
		queryPdfFieldSql = StringUtil.replace(queryPdfFieldSql, "${form_id}", bean.getForm_id().toString());
		Recordset rsField = getDb().get(queryPdfFieldSql);
		
		//返回值赋回form
		bean.setQueryPdfField(rsField);
	}
	
	public void loadFilterShowField(FormBean bean, TemplateBean tmp) throws Throwable{
		if(tmp == null){
			tmp = new TemplateBean(InitializerServlet.getContext());
		}
		String queryFilterFieldSql = tmp.getQueryFilterField();
		queryFilterFieldSql = StringUtil.replace(queryFilterFieldSql, "${form_id}", bean.getForm_id().toString());
		Recordset rsFilterField = getDb().get(queryFilterFieldSql);
		
		String queryShowFieldSql = tmp.getQueryShowField();
		queryShowFieldSql = StringUtil.replace(queryShowFieldSql, "${form_id}", bean.getForm_id().toString());
		Recordset rsShowField = getDb().get(queryShowFieldSql);
		
		//返回值赋回form
		bean.setQueryFilterField(rsFilterField);
		bean.setQueryShowField(rsShowField);
	}

	/**
	 * 加载审计语句
	 * @param bean
	 * @param tmp
	 * @throws Throwable
	 */
	public void loadAuditSql(FormBean bean, TemplateBean tmp) throws Throwable{
		if(tmp == null){
			tmp = new TemplateBean(InitializerServlet.getContext());
		}
		String auditSql = tmp.getInsertAuditSql();
		auditSql = StringUtil.replace(auditSql, "${table_code}", bean.getTable_code());
		auditSql = StringUtil.replace(auditSql, "${form_name}", bean.getForm_name_cn());
		
		String insertAudit = StringUtil.replace(auditSql, "${operation}", "新增");
		String updateAudit = StringUtil.replace(auditSql, "${operation}", "更新");
		String deleteAudit = StringUtil.replace(auditSql, "${operation}", "删除");
		String excelAudit = StringUtil.replace(auditSql, "${operation}", "Excel导出");
		
		bean.setInsert_audit_sql(insertAudit);
		bean.setUpdate_audit_sql(updateAudit);
		bean.setDelete_audit_sql(deleteAudit);
		bean.setExcel_audit_sql(excelAudit);
	}
	
	/**
	 * 加载表单权限
	 * @param bean
	 */
	public void loadOperPriviledge(FormBean bean){
		String priviledge = bean.getOper_priviledge();
		if(priviledge != null){
			Map<String, String> skillMap = bean.getSkillMap();
			String[] ps = priviledge.split(",");
			for(String p : ps){
				String[] sp = p.split(":");
				if(skillMap.containsKey(sp[0])){
					skillMap.put(sp[0], skillMap.get(sp[0])+sp[1]);
				}else{
					skillMap.put(sp[0], sp[1]);
				}
			}
			bean.setSkillMap(skillMap);
		}
	}
	
	/**
	 * 验证某个用户是否有操作权限，只要有一个技能拥有权限就认为通过
	 * @param bean
	 * @param user
	 * @param type
	 * @return
	 */
	public static boolean hasPriviledge(FormBean bean, DinamicaUser user, String type){
		boolean isHas = false;
		Map<String, String> skillMap = bean.getSkillMap();
		String[] skills = user.getRoles();
		if(skills != null){
			for(String skill : skills){
				if(skillMap.containsKey(skill)){
					String p = skillMap.get(skill);
					if(p.indexOf(type) >= 0){
						isHas = true;
						break;
					}
				}
			}
		}
		return isHas;
	}
}
