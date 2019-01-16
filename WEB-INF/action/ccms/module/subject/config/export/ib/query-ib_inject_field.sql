select
	t.*
from 
	cc_ib_inject_field t
where 
	 t.job_id = ${fld:job_id}