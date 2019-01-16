select
	role_id
from 
	${schema}s_user_role
where 
	user_id = ${fld:id}
