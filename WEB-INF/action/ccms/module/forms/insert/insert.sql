INSERT	INTO
t_form
(
	tuid
	, subject_id
	, table_id
	, form_name
	, form_name_cn
	, form_name_en
	, form_type
	, col_num_edit
	, col_num_filter
	, keypress
	, checkfield
	, form_js
	, addnew_js
	, insert_js
	, update_js
	, loadeditor_js
	, loadfilter_js
	, editback_js
	, search_sql
	, edit_sql
	, remark
	, insert_classname
	, update_classname
	, insert_classname1
	, update_classname1
	, delete_classname
	, insert_classname_validator
	, insert_classname_replace
	, update_classname_validator
	, update_classname_replace
	, delete_classname_validator
	, delete_classname_replace
	, head_inc

	, owner_field
	, form_action
	, search_action
	, access_type
	, operation_type
	, page_size
	, search_success_js
)
VALUES
(
	${seq:nextval@seq_form}
	,${fld:subject_id}
	,${fld:table_id}
	,${fld:form_name_cn}
	,${fld:form_name_cn}
	,${fld:form_name_en}
	,${fld:form_type}
	,${fld:col_num_edit}
	,${fld:col_num_filter}
	,${fld:keypress}
	,${fld:checkfield}
	,${fld:form_js}
	,${fld:addnew_js}
	,${fld:insert_js}
	,${fld:update_js}
	,${fld:loadeditor_js}
	,${fld:loadfilter_js}
	,${fld:editback_js}
	,${fld:search_sql}
	,${fld:edit_sql}
	,${fld:remark}
	,${fld:insert_classname}
	,${fld:update_classname}
	,${fld:insert_classname1}
	,${fld:update_classname1}
	,${fld:delete_classname}
	,${fld:insert_classname_validator}
	,${fld:insert_classname_replace}
	,${fld:update_classname_validator}
	,${fld:update_classname_replace}
	,${fld:delete_classname_validator}
	,${fld:delete_classname_replace}
	,${fld:head_inc}

	,${fld:owner_field}
	,${fld:form_action}
	,${fld:search_action}
	,${fld:access_type}
	,${fld:operation_type}
	,${fld:page_size}
	,${fld:search_success_js}
)