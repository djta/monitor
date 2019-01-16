SELECT 
	a.tuid
	,a.entity_id
	,s.org_post_name as post_name
from 
	hr_authority_list a 
	INNER join hr_org_post s on s.tuid::varchar=a.entity_value 
where
	a.entity_id=${fld:entity_id}
	
${filter}
${orderby}