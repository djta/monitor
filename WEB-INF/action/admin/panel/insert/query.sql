select
	*
from
	${schema}s_panel
where
	service_id = ${fld:service_id} and
	icon_path = ${fld:icon_path} and
	orden = ${fld:orden} 