select
	f.userlogin as id
	,f.name
	,f.userlogin
	,g.org_name
from 
	hr_staff f
	inner join ${schema}s_user s
	on f.userlogin = s.userlogin
	left join hr_org g
	on f.org_id = g.org_id
where 
	s.enabled = 1
and
	f.tenantry_id = ${def:tenantry}

	${filter}

	${orderby}