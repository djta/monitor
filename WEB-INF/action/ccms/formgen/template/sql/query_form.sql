SELECT
	f.tuid as form_id
	,f.form_name_cn as form_name_cn
	,f.form_name_en as form_name_en
	,replace(replace(f.keypress,'${DEF','${def'),'${LBL','${lbl')    as  keypress
	,replace(replace(f.checkfield,'${DEF','${def'),'${LBL','${lbl')    as  checkfield
	,replace(replace(f.form_js,'${DEF','${def'),'${LBL','${lbl')    as  form_js
	,replace(replace(f.addnew_js,'${DEF','${def'),'${LBL','${lbl')    as  addnew_js
	,replace(replace(f.insert_js,'${DEF','${def'),'${LBL','${lbl')    as  insert_js
	,replace(replace(f.update_js,'${DEF','${def'),'${LBL','${lbl')    as  update_js
	,replace(replace(f.loadeditor_js,'${DEF','${def'),'${LBL','${lbl')    as  loadeditor_js
	,replace(replace(f.loadfilter_js,'${DEF','${def'),'${LBL','${lbl')    as  loadfilter_js
	,replace(replace(f.editback_js,'${DEF','${def'),'${LBL','${lbl')    as  editback_js
	,replace(replace(f.search_success_js,'${DEF','${def'),'${LBL','${lbl')    as  search_success_js
	,replace(replace(f.head_inc,'${DEF','${def'),'${INC','${inc')    as  head_inc

	,t.tuid as table_id
	,t.table_code
	,t.table_alias
	,t.bpk_field
	,t.bpk_field_prefix
	,t.bpk_field_seq
	,t.delete_field
	,t.bpk_field_type

	,case when f.delete_classname is null then '' else f.delete_classname end   as    delete_classname  
	,case when f.delete_classname_replace is null then '' else f.delete_classname_replace end   as    delete_classname_replace 
	,case when f.delete_classname_validator is null then '' else f.delete_classname_validator end   as    delete_classname_validator
	,case when insert_classname is null then '' else insert_classname end   as    insert_classname  
	,case when insert_classname1 is null then '' else insert_classname1 end   as    insert_classname1  
	,case when insert_classname_replace is null then '' else insert_classname_replace end   as    insert_classname_replace 
	,case when insert_classname_validator is null then '' else insert_classname_validator end   as    insert_classname_validator
	,case when update_classname is null then '' else update_classname end   as    update_classname  
	,case when update_classname1 is null then '' else update_classname1 end   as    update_classname1  
	,case when update_classname_replace is null then '' else update_classname_replace end  as    update_classname_replace 
	,case when update_classname_validator is null then '' else update_classname_validator end   as    update_classname_validator

	,replace(replace(replace(f.edit_sql,'${DEF','${def'),'${INC','${inc'),'${FLD','${fld')    as  edit_sql
	,replace(replace(replace(f.search_sql,'${DEF','${def'),'${INC','${inc'),'${FLD','${fld')    as  search_sql
	
	,case when f.col_num_filter is null then 1 else f.col_num_filter end as col_num_filter
	,case when f.col_num_edit is null then 1 else f.col_num_edit end as col_num_edit
	,f.subject_id

	,f.oper_priviledge
	,tf.field_code as owner_field
	,case when f.form_action is null then '0' else f.form_action end as form_action
	,case when f.search_action is null then '0' else f.search_action end as search_action
	,f.access_type
	,f.operation_type
	,case when f.page_size is null then 20 else f.page_size end as page_size
FROM
	t_form f
	inner join t_table t on f.table_id=t.tuid
	left join t_field tf on concat(tf.tuid,'') = f.owner_field
WHERE
	f.tuid = ${form_id}