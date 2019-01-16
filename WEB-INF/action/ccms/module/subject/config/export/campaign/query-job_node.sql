select
	f.*
from 
	cs_job t
	inner join cs_job_node f
	on t.tuid = f.job_id
where 
	t.campaign_id = ${fld:campaign_id}