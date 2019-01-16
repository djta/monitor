select 
	distinct 
	s.service_id
from
	${schema}s_service s,
	${schema}s_service_role sr,
	${schema}s_role r
where 
	r.role_id = sr.role_id
and 
	r.role_id = ${fld:id}
and 
	s.service_id = sr.service_id
