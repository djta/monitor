select
	service_id as id,
	description
from
	${schema}s_service
where
	service_id = ${fld:service_id}