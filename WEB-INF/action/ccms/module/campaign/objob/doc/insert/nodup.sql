select 
	1
from 
	cs_job_form t
where
	t.job_id=${fld:job_id}
and 
	t.document_id=${fld:document_id}
