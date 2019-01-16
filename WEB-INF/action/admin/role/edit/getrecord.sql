select	
	app_id,
	rolename,
	description,
	role_id
from 
	${schema}s_role
where 
	role_id = ${fld:id}
