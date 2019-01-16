select
	d.*
from 
	t_import_table t
	inner join t_import_field d
	on t.tuid = d.tab_id
where 
	t.imp_id = ${fld:imp_id}