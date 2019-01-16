select
	t.tuid as old_item_id
	,${seq:nextval@seq_form_item} as item_id
from
	t_form_item t
where
	t.form_id = ${form_id}