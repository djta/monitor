SELECT 
	t.tuid
	,t.entity_id
	,t.entity_value as post_id
	,hp.post_name 
from 
	hr_authority_list t
	left join hr_post hp on hp.post_id::varchar=t.entity_value
where
	entity_id=${fld:entity_id}