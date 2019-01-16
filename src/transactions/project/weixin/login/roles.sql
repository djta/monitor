select 
	r.role_id as roleid
from
	${schema}s_application a, 
	${schema}s_role r,
	${schema}s_user_role ur
where
	a.app_alias = '${application}'
and
	r.app_id = a.app_id
and
	ur.role_id = r.role_id 
and
	ur.user_id = ${fld:user_id}
	