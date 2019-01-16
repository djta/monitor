select
	role_id,
	rolename,
	${schema}s_role.app_id,
	${schema}s_role.description,
	${schema}s_application.description as appname
from 
	${schema}s_role, ${schema}s_application
where 
	${schema}s_role.app_id = ${schema}s_application.app_id

	${filter}
    ${orderby}