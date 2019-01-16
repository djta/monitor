select
	t.bpk_field_alias
	,d.bpk_field_type
from
	t_import_table t
	inner join t_table d
	on t.table_id = d.tuid
where
	t.imp_id = ${fld:imp_id}