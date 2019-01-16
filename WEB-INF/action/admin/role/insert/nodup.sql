select 
	rolename 
from 
	${schema}s_role
where 
	rolename = ${fld:rolename}
and 
	app_id = ${fld:app_id}
