select
	app_id as id,
	app_alias
from	
	${schema}s_application
where
	app_id = ${fld:id}