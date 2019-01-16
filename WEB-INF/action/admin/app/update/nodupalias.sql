select 
	app_alias 
from 
	${schema}s_application
where 
	app_id <> ${fld:tuid}
and 
	app_alias = ${fld:app_alias}
