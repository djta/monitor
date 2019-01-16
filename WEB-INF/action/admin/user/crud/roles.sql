select
	r.role_id,
	r.rolename,
	a.app_alias,
	r.description
from 
	${schema}s_role r
inner join 
	${schema}s_application a
on 
	r.app_id = a.app_id
order by 
	a.app_alias,r.rolename
