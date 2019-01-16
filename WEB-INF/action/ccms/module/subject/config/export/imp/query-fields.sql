select
	distinct
	d.*
from 
	t_table t
	inner join t_import_table f
	on t.tuid = f.table_id
	inner join t_field d
	on d.table_id = t.tuid
where 
	f.imp_id = ${fld:imp_id}
