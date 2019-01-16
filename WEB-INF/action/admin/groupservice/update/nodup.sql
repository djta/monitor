select
	group_name
from
	${schema}s_service_group
where
	app_id = ${fld:app_id}
and
	group_name = ${fld:group_name}
and 
	group_id<>${fld:tuid}
