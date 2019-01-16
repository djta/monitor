select
	1
from
	${schema}s_menu
where
	position <> ${fld:position} and
	title_cn = ${fld:title_cn} and
	app_id = ${fld:app_id}
