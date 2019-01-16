select 
	p.userlogin    as  id
	, p.name as description
	, p.userlogin as name
from 
	hr_staff p
where
1=1
	
	${filter}
	${orderby}