select
	menu_id as id,
	title
from
	${schema}s_menu
where
	menu_id = ${fld:id}