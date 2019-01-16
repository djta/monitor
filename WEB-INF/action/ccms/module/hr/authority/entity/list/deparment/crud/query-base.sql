SELECT 
	t.tuid
	,s.org_id
	,t.entity_id
	,t.entity_value
	,s.org_name as org_name
from 
	hr_authority_list t
	left join hr_org s on s.org_id::varchar=t.entity_value
where
	entity_id=${fld:entity_id}
	
