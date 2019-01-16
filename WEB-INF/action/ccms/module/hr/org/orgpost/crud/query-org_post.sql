select 
	t.tuid
	,t.org_post_name
from 
	hr_org_post t
where 
	t.org_id=${fld:org_id}