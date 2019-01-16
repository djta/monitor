select
	service_id,
	path,
	description_cn,
	description_en
from 
	${schema}s_service
where 
	app_id = (select app_id from ${schema}s_menu where menu_id=${fld:menu_id})

	${filter}

	${orderby}