UPDATE
	t_form
SET
	subject_id     =${fld:subject_id}
	,table_id     =${fld:table_id}
	,form_name     =${fld:form_name_cn}
	,form_name_cn     =${fld:form_name_cn}
	,form_name_en     =${fld:form_name_en}
	,form_type     =${fld:form_type}
	,col_num_edit     =${fld:col_num_edit}
	,col_num_filter     =${fld:col_num_filter}
	,keypress	 =${fld:keypress}
	,checkfield	 =${fld:checkfield}
	,form_js	 =${fld:form_js}
	,addnew_js	 =${fld:addnew_js}
	,insert_js	 =${fld:insert_js}
	,update_js	 =${fld:update_js}
	,loadeditor_js	 =${fld:loadeditor_js}
	,loadfilter_js	=${fld:loadfilter_js}
	,editback_js	 =${fld:editback_js}
	,search_sql	 =${fld:search_sql}
	,edit_sql	 =${fld:edit_sql}
	,remark	 =${fld:remark}
	,insert_classname = ${fld:insert_classname}
	,update_classname = ${fld:update_classname}
	,insert_classname1 = ${fld:insert_classname1}
	,update_classname1 = ${fld:update_classname1}
	,delete_classname = ${fld:delete_classname}
	,insert_classname_validator = ${fld:insert_classname_validator}
	,insert_classname_replace = ${fld:insert_classname_replace}
	,update_classname_validator = ${fld:update_classname_validator}
	,update_classname_replace = ${fld:update_classname_replace}
	,delete_classname_validator = ${fld:delete_classname_validator}
	,delete_classname_replace = ${fld:delete_classname_replace}
	,head_inc = ${fld:head_inc}

	,owner_field = ${fld:owner_field}
	,access_type     =${fld:access_type}
	,operation_type     =${fld:operation_type}
	,page_size     =${fld:page_size}
	,form_action = ${fld:form_action}
	,search_action = ${fld:search_action}
	,search_success_js = ${fld:search_success_js}
WHERE
	tuid	=${fld:tuid}
