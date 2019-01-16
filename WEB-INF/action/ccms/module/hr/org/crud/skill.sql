select
	skill_id,
	skill_name
from 
	hr_skill
where
	tenantry_id = ${def:tenantry}
order by skill_name
