select 
	max(job_id) as job_id
from
	cs_job_filter
where
	job_id = ${fld:job_id}
