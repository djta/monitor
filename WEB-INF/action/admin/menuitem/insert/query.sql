select
	*
from
	${schema}s_menu_item  
where
	service_id = ${fld:service_id} and
	menu_id = ${fld:menu_id}