select
	distinct
	t.*
from 
	t_table t
	inner join t_import_table f
	on t.tuid = f.table_id
where 
	f.imp_id = ${fld:imp_id}
