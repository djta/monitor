select 
	p.hc_id    as  id
	, p.hc_name as description
from 
	hr_headcount p
where
	1=1
and
not exists(
	select 1 from hr_staff  where p.hc_id=hr_staff.hc_id
)
	${filter}
	${orderby}