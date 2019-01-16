SELECT
	tuid
	,tab_id
	,field_id
	,field_code
	,col_name
	,domain_namespace
	,update_mode
	,field_type
	,(select name_alias from t_field_type where name=p.field_type) as field_name
	,field_length
	,is_mandatory
	,show_order
	,is_virtual_type
	,default_value
	,fk_schema
	,fk_tab
	,fk_fld_code
	,fk_fld_name
	,remark
	,is_formula 
	,is_save_code
	,regex_rule
	,regex_tip
	,show_align
FROM
	t_import_field p
WHERE
	p.tuid=${fld:id}
	
