select
	t.*
from 
	cs_job t
where 
	t.campaign_id = ${fld:campaign_id}