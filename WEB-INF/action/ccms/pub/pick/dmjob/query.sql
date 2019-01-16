select 
	tuid    as  id
	, job_name  as description
from 
	cc_dm_job
where
	is_enabled = '1'
and
	subject_id = ${fld:subject_id}
order by 
	id
