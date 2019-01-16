select 
	tuid,
	campaign_name,
	begin_date as begin_date,
	end_date as end_date 
from cs_campaign 
where 
tuid = ${fld:campaign_id}