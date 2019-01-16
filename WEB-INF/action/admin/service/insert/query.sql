select
	service_id,
	path,
	description,
	app_id,
	is_system,
	description_cn,
	description_en,
	group_id
from
	${schema}s_service
where
	path = ${fld:path}
