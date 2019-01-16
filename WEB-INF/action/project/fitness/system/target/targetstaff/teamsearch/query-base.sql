select 
	staff.user_id,
	staff.userlogin,
	staff.name
from hr_staff staff
left join hr_team_staff ts on staff.user_id = ts.user_id::int 
where staff.org_id = ${def:org} 
and ts.team_id = ${fld:skill_id}
and staff.status=1
