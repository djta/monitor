select 
	hs.user_id as tuid
	,hs.userlogin
	,hs.name
	,hs.hc_id 
from 
	hr_staff hs
where 
	hc_id is null 
	and 
	org_id=${fld:org_id} 
	
	${filter}
	${orderby}
