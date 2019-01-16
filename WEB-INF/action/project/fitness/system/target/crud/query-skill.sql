select 
	skill_id,
	skill_name,
	skill_scope
from
	hr_skill
where skill_scope in ('0','1','2') and org_id = ${def:org}