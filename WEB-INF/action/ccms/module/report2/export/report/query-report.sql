select
	f.*
from 
	t_report f
where 
	f.tuid = ${fld:report_id}
