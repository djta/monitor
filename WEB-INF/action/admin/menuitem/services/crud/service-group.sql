select 
	group_id
	,group_name
from 
	${schema}s_service_group
where 
	app_id = 1
order by
	group_name