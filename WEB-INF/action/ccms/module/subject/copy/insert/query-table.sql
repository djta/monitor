select
	t.tuid as old_table_id
	,${seq:nextval@seq_table} as table_id
from
	t_table t
where
	t.subject_id = ${fld:subject_id}