select 
	t.tuid
	,t.field_code
	,t.field_name_cn
	,t.field_length
	,t.default_value
	,t.domain_namespace
	,t.field_type
	,(select name_alias from t_field_type where name=t.field_type) as field_name
	,t.show_order
	,t.fk_schema
	,t.fk_tab
	,t.fk_fld_id as fk_fld_code
	,t.is_mandatory
from 
	t_field t
where 
	t.table_id = (select table_id from t_import_table where tuid = ${fld:tab_id})
and is_virtual_type = '0'
and t.deleted = '0'
	${filter}