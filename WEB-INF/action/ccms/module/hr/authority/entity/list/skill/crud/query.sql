SELECT 
	t.tuid
	,t.entity_id 
	,t.entity_value as skill_id
	,hs.skill_name
from 
	hr_authority_list t
	left join hr_skill hs on hs.skill_id::varchar=t.entity_value
where
	entity_id=${fld:entity_id}

