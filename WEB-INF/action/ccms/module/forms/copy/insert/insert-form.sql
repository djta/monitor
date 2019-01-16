INSERT	INTO
t_form
(
	tuid, subject_id, table_id, form_name, pid, deleted, enabled, 
       remark, created, createdby, updated, updatedby,  col_num_edit, 
       col_num_filter, form_type, insert_classname, update_classname, 
       delete_classname, checkfield, keypress, form_js, relation_type, 
       search_sql, show_type, addnew_js, loadeditor_js, editback_js, 
       form_action, search_action, form_inc, filter_inc, view_inc, 
       insert_js, form_name_en, update_js, edit_sql, insert_classname1, 
       update_classname1, insert_classname_replace, update_classname_replace, 
       delete_classname_replace, insert_classname_validator, update_classname_validator, 
       delete_classname_validator, loadfilter_js, form_name_cn, count_sql, 
       owner_field, insert_inc, update_inc, delete_inc, edit_inc, head_inc, oper_priviledge, access_type, operation_type, page_size, search_success_js
)
select
	${form_id}
       ,subject_id, table_id, concat(${fld:form_name},' - Copy'), pid, deleted, enabled, 
       remark, created, createdby, updated, updatedby,  col_num_edit, 
       col_num_filter, form_type, insert_classname, update_classname, 
       delete_classname, checkfield, keypress, form_js, relation_type, 
       search_sql, show_type, addnew_js, loadeditor_js, editback_js, 
       form_action, search_action, form_inc, filter_inc, view_inc, 
       insert_js, ${fld:form_name}, update_js, edit_sql, insert_classname1, 
       update_classname1, insert_classname_replace, update_classname_replace, 
       delete_classname_replace, insert_classname_validator, update_classname_validator, 
       delete_classname_validator, loadfilter_js, ${fld:form_name}, count_sql, 
       owner_field, insert_inc, update_inc, delete_inc, edit_inc, head_inc, oper_priviledge, access_type, operation_type, page_size, search_success_js
from
	t_form
where
	tuid = ${fld:form_id}