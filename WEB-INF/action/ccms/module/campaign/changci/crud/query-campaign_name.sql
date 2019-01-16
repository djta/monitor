select
	tuid as campaign_id,  
	campaign_name 
from  
	cs_campaign 
where 
	tuid=${fld:campaign_id}
order by
	priority
