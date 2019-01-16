select 
	title
	,menu_id
from 
	${schema}s_menu
where 
	menu_id = ${fld:menu_id}
