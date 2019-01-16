select 
	service_id,
	path,
	description,
	description_cn,
	description_en,
	app_id
from
	${schema}s_service
where
	service_id = ${fld:id}


