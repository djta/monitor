select
	f.tuid as tab_id
	,f.parent_id
	,f.bpk_field_alias
	,f.if_new_flag
	,t.schema_name
	,t.table_code
	,f.table_id
	,t.subject_id
	,t.bpk_field
	,t.bpk_field_prefix
	,t.bpk_field_seq
	,t.bpk_field_type
	,f.data_operator_type
from
	t_import_table f
	inner join t_table t
	on t.tuid = f.table_id
where
	f.parent_id = ${parent_id}