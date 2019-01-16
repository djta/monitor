select
	m.menu_item_id as id,
	m.service_id,
	description
from
	${schema}s_menu_item m,
	${schema}s_service s
where
	m.menu_item_id = ${fld:id} and
	m.service_id = s.service_id