select
	role_id,
	description
from 
	${schema}s_role
where 
	app_id = ${fld:app_id}
order by 
	rolename
