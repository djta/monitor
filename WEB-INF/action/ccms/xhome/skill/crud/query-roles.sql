select
	r.role_id,
	r.rolename
from 
	${schema}s_role r
where 
	r.role_id not in (1,2)
order by 
	r.rolename
