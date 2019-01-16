select
	t.*
from 
	cs_campaign_dealer t
where 
	t.campaign_id = ${fld:campaign_id}