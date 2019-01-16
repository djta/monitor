select
	t.*
from 
	cc_ib_doc t
where 
	 t.job_id = ${fld:job_id}
