select 
	team_id,
	team_name,
	skill_scope
from
	hr_team
where skill_scope in ('0','1','2') and org_id=${def:org}