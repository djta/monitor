select 
	p.tuid    as  id
	, p.campaign_name as description
	, p.campaign_name_en as name
from 
	cs_campaign p
where
	p.subject_id = ${fld:subject_id}
	
	${filter}
	${orderby}