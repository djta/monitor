select
	f.*
from 
	t_report f
	inner join t_document d on d.report_id = f.tuid
where 
	d.tuid = ${fld:document_id}