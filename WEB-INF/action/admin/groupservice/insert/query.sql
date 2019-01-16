select
	group_id,
	app_id,
	group_name
from
	${schema}s_service_group
where
	app_id = ${fld:app_id}