select 
	p.tuid    as  id
	, p.job_name as description
from 
	cs_job p
where
	p.campaign_id = ${fld:campaign_id}
and
	p.parent_id is null

	${filter}
	${orderby}