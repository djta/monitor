select 
	rolename 
from 
	${schema}s_role
where 
	role_id <> ${fld:tuid}
and 
	rolename = ${fld:rolename}
and 
	app_id = ${fld:app_id}