select
	f.*
from 
	cc_ib_job f
where 
	f.tuid = ${fld:job_id}