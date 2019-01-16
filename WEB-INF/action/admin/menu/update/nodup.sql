select
	*
from
	${schema}s_menu
where
	
	menu_id <> ${fld:tuid} and
	app_id <> ${fld:app_id} and
	title_cn = ${fld:title_cn} and
	position = ${fld:position}
