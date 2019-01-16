select 
	a.menu_item_id, 
	a.position, 
	b.description_cn,
	b.description
from 
	${schema}s_menu_item a
	inner join ${schema}s_service b
	on a.service_id = b.service_id
where
	a.menu_id = ${fld:menu_id}

	${filter}

	${orderby}
