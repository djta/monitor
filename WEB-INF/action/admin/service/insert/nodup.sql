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
and
	app_id = (select app_id from ${schema}s_service_group where group_id=${fld:group_id})
and 
	group_id=${fld:group_id}