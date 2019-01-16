select
		tuid    as  id
	, job_name  as description
from 
	cs_job
where 
    campaign_id = ${fld:campaign_id}
    ${filter}
order by 
	id
