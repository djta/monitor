select 
	role_id,
	app_id,
	rolename,
	description
from 
	${schema}s_role
where 
	rolename = ${fld:rolename}
and 
	app_id = ${fld:app_id}