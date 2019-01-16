select	
	service_id,
	app_id,
	path,
	description_cn,
	description_en,
	description
from ${schema}s_service
where service_id = ${fld:service_id}
order by path
