select 
	p.userlogin    as  id
	, p.name as description
	, p.userlogin as name
from 
	hr_staff p
where
	p.tenantry_id = (select tenantry_id from hr_staff where userlogin='${def:user}')
	
	${filter}
	${orderby}