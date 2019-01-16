select
	t.*
from 
	cs_campaign_changci t
where 
	t.campaign_id = ${fld:campaign_id}