select
	service_id,
	path,
	description_cn,
	description_en,
	description,
	app_id
from
	${schema}s_service
where
	path = ${fld:path} 
and
	app_id = (select app_id from ${schema}s_service_group where group_id=${fld:group_id})
and
	service_id <> ${fld:tuid}
and 
	group_id=${fld:group_id}