select 
	t.skill_name,
	t.skill_code,
	t.remark,
	t.skill_id 
from hr_skill t 
where 1=1 and org_id = ${def:org}

	${filter}
	${orderby}