select 
	staff.user_id,
	staff.userlogin,
	staff.name
from hr_staff staff
left join hr_staff_skill ss on staff.user_id = ss.user_id
where staff.org_id = ${def:org} 
and ss.skill_id = ${fld:skill_id} 
and staff.status=1
