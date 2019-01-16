select
	rolename,
	description
from ${schema}s_role
where app_id = ${fld:webapp}
