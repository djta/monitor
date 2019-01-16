select
	t.*
from 
	t_table t
	inner join t_report f
	on t.tuid = f.table_id
where 
	f.tuid = ${fld:report_id}
