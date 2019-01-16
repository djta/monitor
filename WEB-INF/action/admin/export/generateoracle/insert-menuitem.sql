insert into ${schema}s_menu_item
(menu_item_id, service_id, menu_id, position)

select
	${seq:nextval@${schema}seq_menu_item},
	s.service_id, 
	m.menu_id,
	${fld:position}
from 
	${schema}s_service s,
	${schema}s_menu m
	where 
		s.path = ${fld:path} 
		and s.app_id = appid 
		and m.title = ${fld:title} 
		and m.app_id = appid;

