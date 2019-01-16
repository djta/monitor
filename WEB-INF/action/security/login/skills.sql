select 
	distinct 
	skill_id
from
	hr_staff_skill
where
	user_id = ${fld:user_id}