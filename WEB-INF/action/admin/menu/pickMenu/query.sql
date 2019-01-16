select 
	menu_id
	,title
	,parentmenu_id as pid
from 
	${schema}s_menu m
where 
	app_id = ${fld:app_id}
and
(	
	${fld:menu_id} is null
	or
	(${fld:menu_id} is not null and m.menu_id <> ${fld:menu_id})
)