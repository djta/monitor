select 
	a.menu_item_id, 
	a.position, 
	b.description, 
	b.service_id,
	b.description_cn,
	b.description_en,
	b.path,
	a.logo_path
from 
	${schema}s_menu_item a,
	${schema}s_service b
where
	a.service_id = b.service_id 
	and a.menu_item_id = ${fld:id}
order by 
	position
