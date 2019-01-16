select	
	role_id,
	app_id,
	rolename,
	description
from 
	${schema}s_role
where 
	role_id = ${fld:id}
