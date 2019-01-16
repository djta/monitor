select
	t.*
from 
	t_import_table t
where 
	t.imp_id = ${fld:imp_id}
