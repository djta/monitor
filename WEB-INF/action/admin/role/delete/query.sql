select
	role_id as id,
	rolename
from
	${schema}s_role
where
	role_id = ${fld:id}