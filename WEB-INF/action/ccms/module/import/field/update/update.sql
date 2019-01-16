UPDATE
	t_import_field
SET
	tab_id = ${fld:tab_id}
	,field_id = ${fld:field_id}
	,field_code = ${fld:field_code}
	,col_name = ${fld:col_name}
	,domain_namespace = ${fld:domain_namespace}
	,update_mode = ${fld:update_mode}
	,field_type = ${fld:field_type}
	,field_length = ${fld:field_length}
	,is_mandatory = ${fld:is_mandatory}
	,show_order = ${fld:show_order}
	,is_virtual_type = ${fld:is_virtual_type}
	,default_value = ${fld:default_value}
	,fk_schema = ${fld:fk_schema}
	,fk_tab = ${fld:fk_tab}
	,fk_fld_code = ${fld:fk_fld_code}
	,fk_fld_name = ${fld:fk_fld_name}
	,remark = ${fld:remark}
	,updated = {ts '${def:timestamp}'}
	,updatedby = '${def:user}'
	,is_formula = ${fld:is_formula}
	,is_save_code = ${fld:is_save_code}
	,regex_rule = ${fld:regex_rule}
	,regex_tip = ${fld:regex_tip}
	,show_align = ${fld:show_align}
WHERE
	tuid = ${fld:tuid}
	
