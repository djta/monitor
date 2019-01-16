insert into ${schema}s_user_role
(user_role_id, user_id, role_id)

select
	${seq:nextval@${schema}seq_user_role},
	${fld:user_id},
	r.role_id 
from 
	${schema}s_role r
	where 
		r.rolename = ${fld:rolename} 
		and r.app_id = ${seq:currval@${schema}seq_application};

		
