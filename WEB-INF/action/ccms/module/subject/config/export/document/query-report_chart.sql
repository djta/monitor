select
	t.*
from 
	t_report_chart t
	inner join t_document d on d.report_id = t.report_id
where 
	d.tuid = ${fld:document_id}