select 
	group_id
	,group_name
from 
	${schema}s_service_group
where 
	app_id = ${fld:app_id}
order by
	group_name