SELECT 
	t.tuid 
	,t.entity_id
	,t.entity_value as team_id
	,ht.team_name 
from 
	hr_authority_list t
	left join hr_team ht on cast(ht.team_id as varchar(4000))=t.entity_value
where
	entity_id=${fld:entity_id}
