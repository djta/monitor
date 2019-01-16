select
	n.*
from 
	cs_job t
	inner join cs_job_node f
	on t.tuid = f.job_id
	inner join cs_job_node_to n
	on f.tuid = n.node_id
where 
	t.campaign_id = ${fld:campaign_id}