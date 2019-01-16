SELECT
	f.tuid
	,f.subject_id
	,f.table_id
	,f.form_name_cn
	,f.form_name_en
	,f.form_type
	,f.col_num_edit
	,f.col_num_filter
	,f.keypress
	,f.checkfield
	,f.form_js
	,f.addnew_js
	,f.insert_js
	,f.update_js
	,f.loadeditor_js
	,f.loadfilter_js
	,f.editback_js
	,f.search_sql
	,f.edit_sql
	,f.remark
	,t.table_alias
	,f.insert_classname
	,f.update_classname
	,f.insert_classname1
	,f.update_classname1
	,f.delete_classname
	,f.insert_classname_replace
	,f.update_classname_replace
	,f.delete_classname_replace
	,f.insert_classname_validator
	,f.update_classname_validator
	,f.delete_classname_validator
	,f.head_inc

	,f.access_type
	,f.operation_type
	,f.page_size
	,f.form_action
	,f.search_action
	,f.search_success_js
	,f.owner_field
	,d.field_name_cn as owner_field_name
FROM
	t_form f
	inner join t_table t on f.table_id = t.tuid
	left join t_field d on f.owner_field = concat(d.tuid,'')
WHERE
	f.tuid=${fld:id}
