select 
	t.skill_name,
	t.remark,
	t.skill_id 
from hr_skill t 
where 1=1 

	${filter}
	${orderby}