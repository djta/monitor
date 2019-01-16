select 
	group_id,
	app_id,
	group_name
from
	${schema}s_service_group
where
	group_id = ${fld:id}


