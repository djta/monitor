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
	group_id=${fld:filter_group_id}

${filter}
${orderby}