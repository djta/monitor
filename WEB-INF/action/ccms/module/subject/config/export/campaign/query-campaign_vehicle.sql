select
	t.*
from 
	cs_campaign_vehicle t
where 
	t.campaign_id = ${fld:campaign_id}