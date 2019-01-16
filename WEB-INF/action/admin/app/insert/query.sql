select 
	app_id,
	app_alias,
	description,
	pwd_policy,
	updated,
	updatedby,
	created,
	createdby
from 
	${schema}s_application
where 
	app_id = ${seq:currval@${schema}seq_application}