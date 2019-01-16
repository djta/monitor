select 
	staff.userlogin,
	staff.name
from hr_staff staff
inner join (select ss.user_id from hr_staff_skill ss
		inner join (select skill_id from hr_skill where skill_scope in('2','4') and org_id = ${fld:org_id}) skill
		on skill.skill_id = ss.skill_id
	) hs on staff.user_id = hs.user_id
where staff.status = 1 
and staff.org_id = ${fld:org_id}