select 
	g.group_id,g.group_name
from 
	${schema}s_service_group g
where
	g.app_id = ${fld:app_id}