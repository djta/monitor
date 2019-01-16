select 
	skill_id    as  id
	, skill_name as description
from 
	hr_skill
where
	tenantry_id = (select tenantry_id from hr_staff where userlogin='${def:user}')
order by 
	skill_name
