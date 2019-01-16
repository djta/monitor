select
	t.*
from 
	t_report_chart t
where 
	t.report_id = ${fld:report_id}
