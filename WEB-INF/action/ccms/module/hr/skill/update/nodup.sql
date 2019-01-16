select 
	skill_name 
from 
	hr_skill
where 
	skill_name = ${fld:skill_name}
and 
	skill_id !=${fld:tuid}
