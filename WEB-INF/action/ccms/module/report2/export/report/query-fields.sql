select
	d.*
from 
	t_table t
	inner join t_report f
	on t.tuid = f.table_id
	inner join t_field d
	on d.table_id = t.tuid
where 
	f.tuid = ${fld:report_id}
