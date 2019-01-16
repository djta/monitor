select
	app_id,
	app_alias,
	description
from ${schema}s_application
where app_id = ${fld:app_id}
order by app_alias
