select 
	p.post_id    as  id
	, p.post_name as description
from 
	hr_post p
where
	1 = 1
	
	${filter}
	${orderby}