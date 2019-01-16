select 
	1
from 
	cs_job_form_template t
where
	t.template_id=${fld:template_id}
and 
	t.document_id=${fld:document_id}
