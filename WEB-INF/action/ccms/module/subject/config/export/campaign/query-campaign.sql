select
	t.*
from 
	cs_campaign t
where 
	t.tuid = ${fld:campaign_id}