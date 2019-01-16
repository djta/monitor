select
	t.tuid as old_field_id
	,${seq:nextval@seq_field} as field_id
from
	t_field t
where
	t.table_id = ${old_table_id}