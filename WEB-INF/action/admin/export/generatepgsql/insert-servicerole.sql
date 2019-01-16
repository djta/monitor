insert into ${schema}s_service_role
(service_role_id, role_id, service_id)

select
	${seq:nextval@${schema}seq_service_role},
	r.role_id, 
	s.service_id

from 
	${schema}s_role r,
	${schema}s_service s
	where 
		r.rolename = ${fld:rolename} 
		and r.app_id = ${seq:currval@${schema}seq_application} 
		and s.path = ${fld:path} 
		and s.app_id = ${seq:currval@${schema}seq_application};

