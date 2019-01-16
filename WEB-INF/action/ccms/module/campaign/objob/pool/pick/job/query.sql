select 
	tuid    as  id
	, job_name  as description
from 
	cs_job
where 
    subject_id = ${fld:subject_id}
order by 
	id
