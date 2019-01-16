select
	b.tuid as tab_id
	,d.table_name
	,b.field_code
	,b.col_name
	,b.domain_namespace
	,b.update_mode
	,b.field_type
	,b.field_length
	,b.is_mandatory
	,b.is_delay
	,b.default_value
	,b.fk_schema
	,b.fk_tab
	,b.fk_fld_code
	,b.fk_fld_name
	,b.is_virtual_type
	,b.is_formula
	,b.is_save_code
	,b.regex_rule
	,b.regex_tip
	,case when b.show_align is null then 'left' else b.show_align end as show_align
from
	t_import_table t
	inner join t_import_field b
	on t.tuid = b.tab_id
	inner join t_table d
	on t.table_id = d.tuid
where
	t.imp_id = ${fld:imp_id}