select
	u.user_id, r.rolename
from ${schema}s_user_role u, ${schema}s_role r
where u.role_id=r.role_id and r.app_id = ${fld:webapp}