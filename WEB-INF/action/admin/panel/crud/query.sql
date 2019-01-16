select 
	b.path, 
	ap.app_alias as alias,
	b.service_id,
	b.description
from 
	${schema}s_menu_item a,
	${schema}s_service b,
	${schema}s_menu m,
	${schema}s_application ap
where
	a.menu_id = m.menu_id and 
	a.service_id = b.service_id and
	ap.app_id=m.app_id and
	m.app_id = ${fld:app_id} 
order by 
	a.position
