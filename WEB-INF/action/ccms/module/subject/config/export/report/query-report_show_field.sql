select
	t.*
from 
	t_report_show_field t
where 
	t.report_id = ${fld:report_id}
