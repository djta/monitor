select
	service_id as id,
	path
from
	${schema}s_service
where
	service_id = ${fld:id}