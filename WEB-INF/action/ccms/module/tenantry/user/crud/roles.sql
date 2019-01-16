select
	r.role_id,
	r.rolename,
	a.app_alias,
	r.description
from 
	${schema}s_role r
	inner join ${schema}s_application a on r.app_id = a.app_id
	inner join ${schema}s_user_role ur on r.role_id=ur.role_id
	inner join ${schema}s_user u on ur.user_id=u.user_id
where 
	u.userlogin = '${def:user}'
order by 
	a.app_alias,r.rolename
