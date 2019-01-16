select 
	t.team_id as tuid,
	t.team_name,
	f.name as team_leader,
	t.remark
from	     
	hr_team t
	left join hr_staff f
	on t.leader_id = f.userlogin
where 
	t.tenantry_id = ${def:tenantry}

	${filter}
	${orderby}