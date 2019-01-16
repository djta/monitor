select
	tuid
	,tab_id
	,field_id
	,field_code
	,(select field_name_${def:locale} from t_field where tuid=t_import_field.field_id) as field_name
	,col_name
	,domain_namespace
	,case when update_mode = '0' then '不更新' when update_mode = '1' then '覆盖更新' when update_mode = '2' then '原值为空更新' when update_mode = '3' then '追加更新' end as update_mode
	,field_type
	,field_length
	,case when is_mandatory = '1' then '是' when is_mandatory = '0' then '否' end as is_mandatory
	,show_order
	,case when is_virtual_type = '1' then '是' when is_virtual_type = '0' then '否' end as is_virtual_type
	,default_value
	,fk_schema
	,fk_tab
	,fk_fld_code
	,fk_fld_name
	,remark
	,created
	,(select name from hr_staff where userlogin=t_import_field.createdby) as createdby
	,updated
	,(select name from hr_staff where userlogin=t_import_field.updatedby) as updatedby
	,case when is_formula = '1' then '是' when is_formula = '0' then '否' end as is_formula
	,case when is_save_code = '1' then '是' when is_save_code = '0' then '否' end as is_save_code
	,case when show_align = 'center' then '居中对齐' when show_align = 'right' then '右对齐' else '左对齐' end as show_align
from
	t_import_field
where
	tab_id=${fld:tab_id_filter}

	${filter}
	${orderby}