select
	t.tuid as old_form_id
	,${seq:nextval@seq_form} as form_id
from
	t_form t
where
	t.table_id = ${old_table_id}