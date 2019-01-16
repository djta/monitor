select 
	hs.user_id
	,hs.userlogin as userlogins 
	,hs.name as names
	,ho.org_name
from 
	hr_staff hs 
	inner join ${schema}s_user s
	on hs.userlogin = s.userlogin
	left join hr_org ho on ho.org_id=hs.org_id 
where 
	hs.tenantry_id = ${def:tenantry}
and
	s.enabled = 1
and
	not EXISTS
		(select 
			1 
			from hr_team_staff hts 
			where hts.team_id=${fld:team_id} and hs.userlogin=hts.staff_id)	
	
	${filter}
	${orderby}